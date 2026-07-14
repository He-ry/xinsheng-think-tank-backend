package com.learn.service.maxkb;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.config.maxkb.MaxkbProperties;
import com.learn.domain.dto.MaxKBChatMessageDTO;
import com.learn.domain.dto.WikiSaveDTO;
import com.learn.domain.pojo.LoginUser;
import com.learn.domain.pojo.Result;
import com.learn.domain.pojo.UserContext;
import com.learn.domain.vo.WikiContentVO;
import com.learn.domain.vo.maxkb.MaxKbWikiContentVO;
import com.learn.exception.ServiceException;
import com.learn.models.StreamJsonParser;
import com.learn.models.bo.ContentBO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.enums.WikiStateEnum;
import com.learn.service.thinktank.ThinkTankService;
import com.learn.utils.FileUtils;
import com.learn.utils.SectionConvertUtil;
import jakarta.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Component
public class MaxKbService {
    private static final Logger log = LoggerFactory.getLogger(MaxKbService.class);
    private final Map<String, MaxKbWikiContentVO> stateMap = new ConcurrentHashMap();
    private final Map<String, Boolean> finishMap = new ConcurrentHashMap();
    @Resource
    private MaxkbProperties maxkbProperties;
    @Resource
    private FileUtils fileUtils;
    @Value(value="file.upload-dir")
    private String uploadDir;
    @Lazy
    @Resource
    private ThinkTankService thinkTankService;
    private final StreamJsonParser parser = new StreamJsonParser();
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("```json\\s*(\\{[\\s\\S]*?})\\s*```");

    public MaxKBChatMessageDTO.DocumentInfo uploadFileToMaxKBAndBuildInfo(MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
            try (InputStream in = file.getInputStream();){
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            HttpResponse response = ((HttpRequest)HttpRequest.post((String)(this.maxkbProperties.getHost() + this.maxkbProperties.getApiPath().getUpload())).header("Authorization", "Bearer " + this.maxkbProperties.getApiKey())).form("file", tempFile).form("source_type", (Object)"CHAT").form("source_id", (Object)UUID.randomUUID().toString()).execute();
            tempFile.delete();
            String body = response.body();
            log.info("上传文件到MaxKB: {}", (Object)body);
            ObjectMapper mapper = new ObjectMapper();
            Map respMap = (Map)mapper.readValue(response.body(), Map.class);
            String filePath = (String)respMap.get("data");
            String fileId = filePath.substring(filePath.lastIndexOf("/") + 1);
            long uid = Instant.now().getEpochSecond();
            MaxKBChatMessageDTO.DocumentInfo fileInfo = new MaxKBChatMessageDTO.DocumentInfo();
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setPercentage(0);
            fileInfo.setStatus("ready");
            fileInfo.setSize(39709L);
            fileInfo.setRaw(Map.of("uid", uid));
            fileInfo.setUid(uid);
            fileInfo.setUrl(filePath);
            fileInfo.setFile_id(fileId);
            return fileInfo;
        }


        catch (Exception e) {
            log.error("上传文件到MaxKB失败", (Throwable)e);
            return null;
        }
    }

    public String getChatId() {
        try {
            String getChatIdPath = this.maxkbProperties.getHost() + this.maxkbProperties.getApiPath().getChat();
            HttpResponse response = ((HttpRequest)HttpRequest.get((String)getChatIdPath).header("Authorization", "Bearer " + this.maxkbProperties.getApiKey())).execute();
            log.info("获取会话ID成功: {}", (Object)response.body());
            ObjectMapper mapper = new ObjectMapper();
            Map respMap = (Map)mapper.readValue(response.body(), Map.class);
            return (String)respMap.get("data");
        }


        catch (Exception e) {
            log.error("获取会话ID失败", (Throwable)e);
            return null;
        }
    }

    public String createChat(MaxKBChatMessageDTO maxKBChatMessageDTO) {
        try {
            Object chatPath = this.maxkbProperties.getHost() + this.maxkbProperties.getApiPath().getChatId();
            String chatId = this.getChatId();
            chatPath = ((String)chatPath).replace("chat_id", chatId);
            HttpResponse response = ((HttpRequest)HttpRequest.post((String)chatPath).header("Authorization", "Bearer " + this.maxkbProperties.getApiKey())).body(JSONUtil.toJsonStr((Object)maxKBChatMessageDTO)).execute();
            log.info("生成词条成功: {}", (Object)response.body());
        }


        catch (Exception e) {
            log.error("生成词条失败", (Throwable)e);
        }
        return null;
    }


    public Flux<Object> streamChat(MaxKBChatMessageDTO chatMessageDTO) {
        return Flux.create(sink -> {
            Object chatPath = this.maxkbProperties.getHost() + this.maxkbProperties.getApiPath().getChatId();
            String chatId = this.getChatId();
            chatPath = ((String)chatPath).replace("chat_id", chatId);
            try {
                String line;
                HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post((String)chatPath).header("Authorization", "Bearer " + this.maxkbProperties.getApiKey())).header("Content-Type", "application/json")).body(JSONUtil.toJsonStr((Object)chatMessageDTO)).executeAsync();
                InputStream inputStream = response.bodyStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    sink.next((Object)line);
                }
                reader.close();
                inputStream.close();
                sink.complete();
            }


            catch (Exception e) {
                sink.error((Throwable)e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }


    public String chatSync(ThinkTankDO thinkTankDO, MaxKBChatMessageDTO chatMessageDTO) {
        String id = thinkTankDO.getId();
        MaxKbWikiContentVO maxKbWikiContentVO = (MaxKbWikiContentVO)this.stateMap.get(id);
        StringBuilder rawResult = new StringBuilder();
        String chatPath = this.maxkbProperties.getHost() + this.maxkbProperties.getApiPath().getChatId().replace("chat_id", this.getChatId());
        try {
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post((String)chatPath).header("Authorization", "Bearer " + this.maxkbProperties.getApiKey())).header("Content-Type", "application/json")).body(JSONUtil.toJsonStr((Object)chatMessageDTO)).executeAsync();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) continue;
                    JSONObject obj = JSONUtil.parseObj((String)line.substring(5).trim());
                    if (obj.getBool((Object)"is_end", Boolean.valueOf(false)).booleanValue()) {
                        break;
                    }
                    String token = obj.getStr((Object)"content");
                    if (StrUtil.isBlank((CharSequence)token)) continue;
                    rawResult.append(token);
                    this.parser.append(id, token, maxKbWikiContentVO);
                    log.info("更新状态: {}", (Object)maxKbWikiContentVO);
                    maxKbWikiContentVO.setTotalWords(SectionConvertUtil.getTotalWords((String)thinkTankDO.getTitle(), (List)maxKbWikiContentVO.getContent().getSections()));
                    maxKbWikiContentVO.setChaptersNum(SectionConvertUtil.getChapterNumber((List)maxKbWikiContentVO.getContent().getSections()));
                    this.stateMap.put(id, maxKbWikiContentVO);
                }
            }
        }

        catch (Exception e) {
            log.error("模型生成失败", (Throwable)e);
        }
        this.finishMap.put(id, true);
        maxKbWikiContentVO.setState(WikiStateEnum.COMPLETED.getCode());
        maxKbWikiContentVO.setStateMessage(WikiStateEnum.COMPLETED.getDesc());
        this.stateMap.put(id, maxKbWikiContentVO);
        this.parser.clear(id);
        log.info("模型生成成功: {}", (Object)rawResult);
        return rawResult.toString();
    }

    public Result<HashMap<String, Object>> getFluxContent(String thinkTankId) {
        Boolean b = (Boolean)this.finishMap.get(thinkTankId);
        HashMap<String, MaxKbWikiContentVO> map = new HashMap<String, MaxKbWikiContentVO>();
        if (b == null) {
            MaxKbWikiContentVO maxKbWikiContentVOFromDb = this.getMaxKbWikiContentVOFromDb(thinkTankId);
            map.put("entry", maxKbWikiContentVOFromDb);
            return new Result(Integer.valueOf(200), maxKbWikiContentVOFromDb.getStateMessage(), map);
        }
        MaxKbWikiContentVO maxKbWikiContentVO = (MaxKbWikiContentVO)this.stateMap.get(thinkTankId);
        if (!b.booleanValue()) {
            map.put("entry", maxKbWikiContentVO);
            return new Result(Integer.valueOf(102), "模型生成中...", map);
        }
        MaxKbWikiContentVO maxKbWikiContentVOFromDb = this.getMaxKbWikiContentVOFromDb(thinkTankId);
        map.put("entry", maxKbWikiContentVOFromDb);
        return new Result(Integer.valueOf(200), WikiStateEnum.COMPLETED.getDesc(), map);
    }


    private MaxKbWikiContentVO getMaxKbWikiContentVOFromDb(String thinkTankId) {
        WikiContentVO thinkTank = this.thinkTankService.getThinkTank(null, thinkTankId);
        MaxKbWikiContentVO maxKbWikiContentVO = new MaxKbWikiContentVO();
        BeanUtils.copyProperties((Object)thinkTank, (Object)maxKbWikiContentVO);
        maxKbWikiContentVO.setState(thinkTank.getState());
        maxKbWikiContentVO.setStateMessage(WikiStateEnum.fromCode((String)thinkTank.getState()).getDesc());
        maxKbWikiContentVO.setCreatorName(thinkTank.getCreatorName());
        return maxKbWikiContentVO;
    }


    public Map<String, Object> parseMdWithLastJsonFence(String content) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (content == null || content.isBlank()) {
            result.put("markdown", "");
            result.put("json", null);
            return result;
        }
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(content);
        if (matcher.find()) {
            String json = matcher.group(1).trim();
            String markdown = content.substring(0, matcher.start()).trim();
            result.put("markdown", markdown);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                WikiSaveDTO dto = (WikiSaveDTO)mapper.readValue(json, WikiSaveDTO.class);
                result.put("wiki", dto);
            }


            catch (JsonProcessingException e) {
                log.error("解析 JSON 失败", (Throwable)e);
            }
        } else {
            result.put("markdown", content.trim());
            result.put("wiki", null);
        }
        return result;
    }


    public String aiCreateEntry(String title, String prompt, MultipartFile multipartFile) {
        if (StrUtil.isBlank((CharSequence)prompt)) {
            prompt = "请生成一个关于" + title + "的词条";
        }
        LoginUser loginUser = UserContext.getLoginUser();
        String filePath = this.fileUtils.uploadToLocal(multipartFile, this.uploadDir);
        log.info("filePath: {}", (Object)filePath);
        this.checkExist(title);
        ThinkTankDO thinkTankDO = ThinkTankDO.builder().title(title).canonicalUrl("/page/" + title).createAt(Long.valueOf(Instant.now().getEpochSecond())).creator(loginUser.getUserId().toString()).updateAt(Long.valueOf(Instant.now().getEpochSecond())).state(WikiStateEnum.BUILDING.getCode()).files(JSONUtil.toJsonStr(List.of(filePath))).stateMessage("ai生成中").build();
        this.thinkTankService.save((Object)thinkTankDO);
        this.initAiThinkTankMap(thinkTankDO);
        Object finalPrompt = prompt;
        new Thread(() -> this.lambda$aiCreateEntry$1(loginUser, multipartFile, thinkTankDO, (String)finalPrompt, title)).start();
        return thinkTankDO.getId();
    }


    private void initAiThinkTankMap(ThinkTankDO thinkTankDO) {
        String id = thinkTankDO.getId();
        ContentBO content = new ContentBO();
        content.setSummary("");
        content.setSections(new ArrayList());
        MaxKbWikiContentVO state = new MaxKbWikiContentVO();
        state.setId(id);
        state.setTitle(thinkTankDO.getTitle());
        state.setCanonicalUrl(thinkTankDO.getCanonicalUrl());
        state.setCreateAt(thinkTankDO.getCreateAt());
        state.setUpdateAt(thinkTankDO.getUpdateAt());
        state.setCreator(thinkTankDO.getCreator());
        state.setTotalWords(Long.valueOf(0L));
        state.setTotalCitationsNum(Integer.valueOf(0));
        state.setChaptersNum(new ArrayList());
        state.setCreatorName(UserContext.getUsername());
        state.setState(WikiStateEnum.BUILDING.getCode());
        state.setStateMessage(WikiStateEnum.BUILDING.getDesc());
        state.setContent(content);
        state.setCitations(new ArrayList());
        state.setRelatedEntries(new ArrayList());
        this.finishMap.put(id, false);
        this.stateMap.put(id, state);
    }


    private void checkExist(String title) {
        if (CollUtil.isNotEmpty((Collection)((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().select(new SFunction[]{ThinkTankDO::getTitle}).eq(ThinkTankDO::getTitle, (Object)title)).list())) {
            throw new ServiceException("词条已存在");
        }
    }

    private void fail(String id, String msg, Exception e) {
        log.error("ThinkTank build failed, id={}", (Object)id, (Object)e);
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.thinkTankService.lambdaUpdate().eq(ThinkTankDO::getId, (Object)id)).set(ThinkTankDO::getState, (Object)WikiStateEnum.BUILDING_FAILED.getCode())).set(ThinkTankDO::getStateMessage, (Object)msg)).update();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private /* synthetic */ void lambda$aiCreateEntry$1(LoginUser loginUser, MultipartFile multipartFile, ThinkTankDO thinkTankDO, String finalPrompt, String title) {
        try {
            UserContext.set((LoginUser)loginUser);
            MaxKBChatMessageDTO.DocumentInfo documentInfo = this.uploadFileToMaxKBAndBuildInfo(multipartFile);
            if (documentInfo == null) {
                this.fail(thinkTankDO.getId(), "文件上传失败", null);
                return;
            }
            MaxKBChatMessageDTO messageDTO = MaxKBChatMessageDTO.builder().message(finalPrompt).re_chat(false).stream(true).document_list(Collections.singletonList(documentInfo)).audio_list(Collections.emptyList()).image_list(Collections.emptyList()).video_list(Collections.emptyList()).form_data(Map.of("title", title)).build();
            String chattedSync = this.chatSync(thinkTankDO, messageDTO);
            if (chattedSync == null) {
                this.fail(thinkTankDO.getId(), "模型生成词条失败", null);
                return;
            }
            Map result = this.parseMdWithLastJsonFence(chattedSync);
            if (result == null || result.get("wiki") == null) {
                this.fail(thinkTankDO.getId(), "AI 返回结果解析失败", null);
                return;
            }
            WikiSaveDTO dto = (WikiSaveDTO)result.get("wiki");
            dto.setId(thinkTankDO.getId());
            this.thinkTankService.aiCreateThinkTank(dto);
            ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.thinkTankService.lambdaUpdate().eq(ThinkTankDO::getId, (Object)thinkTankDO.getId())).set(ThinkTankDO::getState, (Object)WikiStateEnum.COMPLETED.getCode())).set(ThinkTankDO::getStateMessage, (Object)"ai生成成功")).update();
        }


        catch (Exception e) {
            this.fail(thinkTankDO.getId(), "系统异常：" + e.getClass().getSimpleName(), e);
        }
        finally {
            UserContext.clear();
        }
    }
}

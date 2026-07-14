package com.learn.service.thinktank;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.domain.dto.ThinkTankApprovalDTO;
import com.learn.domain.dto.WikiSaveDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.vo.WikiContentRemoveVO;
import com.learn.domain.vo.WikiContentVO;
import com.learn.domain.vo.WikiListVO;
import com.learn.models.entity.ThinkTankDO;
import java.util.List;

import lombok.Data;
public interface ThinkTankService
extends IService<ThinkTankDO> {
    public String createThinkTank(WikiSaveDTO var1);

    public String aiCreateThinkTank(WikiSaveDTO var1);

    public WikiContentRemoveVO deleteThinkTank(String var1);

    public void deleteThinkTankListByIds(List<String> var1);

    public WikiContentVO getThinkTank(String var1, String var2);

    public void addView(String var1);

    public PageResult<WikiListVO> getThinkTankList(String var1, String var2, String var3, String var4, List<String> var5, String var6, String var7);

    public void approval(ThinkTankApprovalDTO var1);
}

import glob, re, os

SRC = r'src\main\java'
errors = []

for f in sorted(glob.glob(os.path.join(SRC, '**', '*.java'), recursive=True)):
    content = open(f, encoding='utf-8').read()
    issues = []

    # Check brace balance
    depth = 0
    for ch in content:
        if ch == '{': depth += 1
        elif ch == '}': depth -= 1
    if depth != 0:
        issues.append(f"BRACE MISMATCH: depth={depth}")

    # Check for garbled lines (multiple statements merged)
    lines = content.split('\n')
    for i, line in enumerate(lines):
        # Detect lines with "}public", "}protected", "}private" (merged methods)
        if re.search(r'\}(?:public|protected|private|@)', line):
            issues.append(f"  Line {i+1}: Merged method/annotation on closing brace")
        # Detect orphaned code: "ClassName other = (ClassName)o;"
        if re.search(r'^\s+\w+ other = \(\w+\)o;', line):
            issues.append(f"  Line {i+1}: Orphaned equals code")
        # Constructor remnants
        if re.search(r'^\s+\w+\(\w+\s+\w+,\s*\w+\s+\w+\);?\s*$', line):
            if not re.search(r'^\s*(?:public|private|protected)\s', line):
                issues.append(f"  Line {i+1}: Possible orphaned line")

    if issues:
        errors.append((f, issues))

print(f"Files with issues: {len(errors)}")
for f, issues in errors:
    print(f"\n{f}:")
    for i in issues:
        print(i)

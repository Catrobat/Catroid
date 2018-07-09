import re
from os import listdir
from os.path import isfile, join

def getFileNames():
    return [f for f in listdir() if isfile(join(f))]

def modifyFile(fileName):
    fileText = open(fileName, "r").read()
    resExpr = re.compile('R.layout.brick_[\S]*')
    resMatch = resExpr.search(fileText)

    if not resMatch:
        return 0

    resourceId = resMatch.group().replace(',', '')
    
    expr = re.compile('public View getView\([\S]* Context')
    
    match = expr.search(fileText)
    if not match:
        return 0
    
    top, bottom = fileText[:match.start()], fileText[match.start():]
    override = '@Override\n' \
               '    protected int getLayoutRes() {\n' \
               '        return '
    override += resourceId #R.layout.brick_ask
    override += ';\n' \
               '    }\n' \
               '\n' \
               '    '
    
    newFileText = top + override + bottom
    file = open(fileName, "w")
    file.write(newFileText)
    file.close()
    return 1
    
def main():
    cnt = 0
    for fileName in getFileNames():
        cnt += modifyFile(fileName)
    #modifyFile("ChangeVariableBrick.java")
    print(cnt)
    
if __name__ == "__main__":
    main()

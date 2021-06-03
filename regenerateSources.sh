echo THIS FILE WILL DELETE YOUR CODE AND RECREATE IT FROM PATCH FILES
echo ""
echo "Patch Files -> Source Code"
echo ""
echo Press enter, to regenerate sources
read
rm mcp -r
mkdir mcp
cd mcp
wget http://mgnet.work/mcp50.zip
unzip mcp50.zip
rm mcp50.zip
/mnt/c/Windows/System32/cmd.exe /C decompile.bat
cd src
find . -type f -exec dos2unix {} \;
find . -type f -exec touch -a -m -t 202101010000.00 {} \;
cd ..
find ../src -type f -exec dos2unix {} \;
patch -s -p0 < ../src/change.patch
cd ..
cp src/tasmod mcp/src/minecraft/net/tasmod -r

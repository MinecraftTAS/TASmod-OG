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
cd ..
dos2unix ../src/change.patch
patch -s -p0 < ../src/change.patch

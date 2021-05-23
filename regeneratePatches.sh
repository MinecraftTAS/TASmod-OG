echo Do you want to create Patch Files?
echo ""
echo "Your Sources -> Patch File -> (Upload to Github)"
echo ""
echo Press enter, to generate Patch Files
read
rm src -r
mkdir src
mkdir temp
cd temp
wget http://mgnet.work/mcp50.zip
unzip mcp50.zip
/mnt/c/Windows/System32/cmd.exe /C decompile.bat
cp ../mcp/src/ ./newsrc -r
find ./newsrc -type f -exec dos2unix {} \;
find ./newsrc -type f -exec touch -a -m -t 202101010000.00 {} \;
find ./src -type f -exec dos2unix {} \;
find ./src -type f -exec touch -a -m -t 202101010000.00 {} \;
diff -ruN src/ newsrc/ > ../src/change.patch
cd ..
rm temp -r
clear
echo Patch File generated, you can now upload this to github

#!/bin/bash
Generated_python_code_dir=$1
Thrift_file_dir=$2
Compress_File_Path=$3
Client=$4
Version=$5
Build_data_dir=$6
Branch=$7
Base_Path=$(cd `dirname $0`; pwd)

echo "<<<<<<<<<<<<<create code dir and thrift dir<<<<<<<<<<<<<<"
rm -rf $Thrift_file_dir/$Client/$Version
rm -rf $Generated_python_code_dir/$Client/$Version
mkdir -p $Thrift_file_dir/$Client/$Version
mkdir -p $Generated_python_code_dir/$Client/$Version

echo "<<<<<<<<<<<<<pull project from git<<<<<<<<<<<<<<<<<<<<<<<"
cd $Thrift_file_dir/$Client/$Version
git clone -b $Branch

if [ -f "$Thrift_file_dir/$Client/$Version/$Client/idl-spec.yaml" ];
then
    cd $Thrift_file_dir/$Client/$Version
    grep "^[^#;]" $Thrift_file_dir/$Client/$Version/$Client/idl-spec.yaml > tmp1.txt
    awk -F ':' '{print $1}' < tmp1.txt > tmp.txt
    while read line;do
    if [ "$line" != "$Client" ];
    then
        git clone
    fi
    done < tmp.txt
    rm -rf *.txt
else
    cd $Thrift_file_dir/$Client/$Version
    git clone

fi

cd $Thrift_file_dir/$Client/$Version
for thname in `find $Thrift_file_dir/$Client/$Version -name "*.yaml"`
do
    grep "^[^#;]" $thname >> tmp1.txt
done
awk -F ':' '{print $1}' < tmp1.txt > tmp.txt
sort -u tmp.txt > toCloneProjectName.txt
while read line;do
if [ "$line" != "$Client" ];
then
    git clone
fi
done < toCloneProjectName.txt
rm -rf *.txt


echo "<<<<<<<<<<<<<generate python code<<<<<<<<<<<<<<<<<<<<<<<<"
cd $Thrift_file_dir/$Client/$Version

for thname in `find $Thrift_file_dir/$Client/$Version -name "*.thrift"`
do
    thrift -r --gen py -out $Generated_python_code_dir/$Client/$Version  $thname
done

echo "<<<<<<<<<<<<<cp remote file to right dir<<<<<<<<<<<<<<<<<"
cd $Generated_python_code_dir/$Client/$Version
find $Generated_python_code_dir/$Client/$Version -name "*-remote" | xargs -I{} cp {} $Generated_python_code_dir/$Client/$Version

echo "<<<<<<<<<<<<<replace import in remote file!!<<<<<<<<<<<<<"
cp $Build_data_dir/my_print.py $Generated_python_code_dir/$Client/$Version
for remotename in `find $Generated_python_code_dir/$Client/$Version -name "*-remote"`
do
    sed  's/import pprint/import my_print as pprint/g' $remotename > tmp
    mv tmp $remotename
done

chmod 774 $Generated_python_code_dir/$Client/$Version/*
echo "<<<<<<<<<<<<<done!!!<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
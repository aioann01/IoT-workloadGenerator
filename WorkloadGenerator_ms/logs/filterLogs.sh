#awk '/$1/' server.log | sed  "s/$1/TRACE/g" > tmp.text
find=$1
showMessage=$2
#awk -v l=$find  '{ gsub(l,"LEL"); print $0 }' server.log  >tmp.txt #|  sed  "s/message: {.*} /""/g"> tmp.txt
if [ $showMessage == true ] 
	then
cat server.log | grep TRACE| grep  "$1" > tmp.txt
	else
#cat server.log | grep TRACE |  grep "$1"| sed  -r "s/message: {.*} /""/g"> tmp.txt
#cat server.log | grep TRACE |  grep "$1"| sed   "s/message: <root>.*<\/root> /""/g"> tmp.txt
cat server.log | grep TRACE |  grep "$1"| sed   "s/message: \({\|<root>\).*\(}\\|<\/root>\) /""/g"> tmp.txt
fi

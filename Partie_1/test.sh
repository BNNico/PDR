nb=60

while [ $nb -ne 0 ]; do
    java  Accessor &
    nb=`expr $nb - 1`
done
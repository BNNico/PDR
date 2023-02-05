nb=5

while [ $nb -ne 0 ]; do
    java  Accessor_2 &
    nb=`expr $nb - 1`
done
java Affiche 
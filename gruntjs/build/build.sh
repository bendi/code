DIST=dist
TMP=webapp-tmp
SRC=webapp

cd ..

if [ -d $TMP ]; then
	rm -rf $TMP
fi
cp -r $SRC $TMP
coffee -bo $TMP $TMP/
cp build/app.build.js $TMP/scripts/

# prepare all coffee-preprocessed modules
perl -i -nle 's/cs!//g;print;' `find $TMP -type f -name '*.js' -or -name '*.html'`

node build/r.js -o $TMP/scripts/app.build.js

# fix all jade-optimized templates
perl -i -nle 's/jade!//g;print;' `find $DIST -type f -name '*.js' -or -name '*.html'`

if [ -f $DIST.tar.gz ]; then
	rm $DIST.tar.gz
fi

for i in `find $DIST/scripts/* -type d`; do
	if [ -d $i ]; then
		rm -rf $i
		echo "Removed dir $i"
	fi
done

if [ -d $DIST/views ]; then
	rm -rf $DIST/views
	echo "Removed dir $DIST/views"
fi

for i in `find $DIST -name '*.js' -not -name require-jquery.js -not -name main.js -or -name *.txt -or -name *.coffee`; do
	if [ -f $i ]; then
		rm $i
		echo "Removed file $i"
	fi
done

if [ -d $TMP ]; then
	rm -rf $TMP
fi

tar czf dist.tar.gz dist
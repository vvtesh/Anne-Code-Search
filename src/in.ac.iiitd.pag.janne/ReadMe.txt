CommonCodeExtractor: Get ids and code from Posts.xml.
PostsReducer(janne): Make Posts.xml short by filtering irrelevant ids.
Now change the filepath in properties file to reduced posts.xml.
Use TFCalculator to compute allCodeTF.txt
Normalize it with TFMaxNormalizer and save as allCodeTFMaxNorm.txt
Now run EntityMiner after copying knownentities and skiplist files.
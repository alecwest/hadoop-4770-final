# hadoop-4770-final
A MapReduce application that will be run in OpenStack

### Setup
Read from step 4.4 [here](https://bigdataproblog.wordpress.com/2016/05/20/developing-hadoop-mapreduce-application-within-intellij-idea-on-windows-10/)
to set up project in Intellij.<br>
To run outside of standalone mode, you'll need to follow [this](http://www.lifeincode.net/programming/hadoop-building-the-jar-of-wordcount-in-intellij-idea/) 
on how to build the jar through Intellij, then execute using 
`hadoop jar classes/articfacts/word_counter_jar/word-counter.jar WordCount <input dir> <output dir>`

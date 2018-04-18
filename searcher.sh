java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction tfidf --score cosine --outputfile result_tfidf.txt --topicfile ./data/topicsTREC8Adhoc.txt --penalize false
java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction bm25 --score cosine --outputfile result_bm25.txt --topicfile ./data/topicsTREC8Adhoc.txt --k1 1.2 --penalize false
java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction bm25va --score cosine --outputfile result_bm25va.txt --topicfile ./data/topicsTREC8Adhoc.txt --k1 1.2 --penalize false

./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_tfidf.txt > evaluation_tfidf.txt
./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_bm25.txt > evaluation_bm25.txt
./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_bm25va.txt > evaluation_bm25va.txt

java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction tfidf --score cosine --outputfile result_penalize_tfidf.txt --topicfile ./data/topicsTREC8Adhoc.txt --penalize true
java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction bm25 --score cosine --outputfile result_penalize_bm25.txt --topicfile ./data/topicsTREC8Adhoc.txt --k1 1.2 --penalize true
java -Xmx4g -jar air_project-1.0-SNAPSHOT-jar-with-dependencies.jar --action query --scorefunction bm25va --score cosine --outputfile result_penalize_bm25va.txt --topicfile ./data/topicsTREC8Adhoc.txt --k1 1.2 --penalize true

./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_penalize_tfidf.txt > evaluation_penalize_tfidf.txt
./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_penalize_bm25.txt > evaluation_penalize_bm25.txt
./trec_eval -q -m map -c data/qrels.trec8.adhoc.parts1-5 result_penalize_bm25va.txt > evaluation_penalize_bm25va.txt

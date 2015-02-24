What is this project about?

This play application is used to build a restful API which takes I/P as a file and:

1. Parse the file (assume any uploaded file is well formatted and contains only ascii characters) for the following information:
2. Returns The total word count
3. Returns The counts of each occurrence of a word
4. Return the parsed information in the HTTP response body as JSON
5. Also, there is another endpoint which can get all the above information for all the uploaded files in the system
6. Remove any words from the response that contain "blue" within then, i.e. "blue," "blueberry," "bluegrass," etc.

What are the Software dependencies:

1. Install activator version 1.2
2. extract the zip file
3. go to context path /file-uploader
4. run the command "activator run"
5. From another command line window:
	1. run the command: curl -F file=@name.txt  -F removeWord=Scala http://localhost:9000/file/upload (passing here file and removeWord)
		this would give the JSON with a map of words with count and total count of words after removing the mentioned word "how"
	2. run the command: curl -F file=@name.txt  http://localhost:9000/file/upload (passing here file only)
		this would give the JSON with a map of words with count and total count of words
	3. run the command: curl http://localhost:9000/file/details
		this would give the json with list of uploaded fileName, there occureneces count of words and total count of word

Test cases:

Behavior driven tests has been added using Scala's Specification framework, following tests are added:

[info] File should
[info] + be uploaded and return OK status
[info] + be uploaded and return the correct word count
[info] + be uploaded and return the correct occurences
 
To run the tests:
1. Go to context path of the project i.e. /file-uploader
2. run the command "activator"
3. run the command "test"
4. Sample test files are lying in the test-data folder

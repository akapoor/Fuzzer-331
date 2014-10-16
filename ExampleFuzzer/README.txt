_______________________________
Chris Timmons
Anshul Kapoor
Fuzzer Project
_______________________________
******
PART 1
******
To run Fuzzer:

args = fuzz discover 'website' '--custom-auth=' '--common-words='

fuzz has to be first argument
discover/test needs to be second. test is not yet implemented
'website' is the website to crawl
'--custom-auth=' needs to be trailed by dvwa or bodgeit
'--common-words=' needs to be followed by mywords.txt or words.txt

******
PART 2
******

***************************HOW TO RUN .JAR FILE***************************

Navigate to root level where Fuzzer_Part2,jar is, and type the exact following command:

java -jar Fuzzer_Part2.jar fuzz test http://127.0.0.1  --custom-auth=dvwa --common-words=words.txt --vectors=vectors.txt --sensitive=sensitive.txt --random=false


Delayed Response: Average of 6.5 seconds is taken as a standard web page load time. If the load time is greater than this, then DoS warning is given.
HTTP Response Codes: Any response code that is not 200, is considered a bad response code and is warned to the user.
Sensitive Data Leaks: sensative.txt contains possible list of sensitive data. This set of data is check in the response and request part at every page load.
					  If a match is found, then the user is warned.
Lack of Sanitization: Possible set of exploit vectors are fuzzed into the input fields at every page, and their output is observed. If a bad response is found, the user is warned.

The complete program takes ~5min to run with the current file inputs. It fuzzes all the discovered pages with above inputs while doing so.

import requests
import sys
import time
import json
#Constants
HTTPS_PROTOCOL = "http://"
WORKLOAD_GENERATOR_PORT = str(8091)
WORKLOAD_GENERATOR_RESOURCE = "/workloadGenerator"
HTTP_TESTING_RESOURCE = "/testing"
HTTP_SERVER_PORT = str(8095)
host =  sys.argv[1]
waitTime =  int(sys.argv[2])
messageIntervalTime = int(sys.argv[3])
workloadGeneratorStartURL = HTTPS_PROTOCOL + host + ":" + WORKLOAD_GENERATOR_PORT + WORKLOAD_GENERATOR_RESOURCE + "/start"
workloadGeneratorRestartURL = HTTPS_PROTOCOL + host + ":" + WORKLOAD_GENERATOR_PORT + WORKLOAD_GENERATOR_RESOURCE + "/restart"
workloadGeneratorStopURL = HTTPS_PROTOCOL + host + ":" + WORKLOAD_GENERATOR_PORT + WORKLOAD_GENERATOR_RESOURCE + "/stop"
httpServerResetURL =  HTTPS_PROTOCOL + host + ":" + HTTP_SERVER_PORT + HTTP_TESTING_RESOURCE + "/reset"
httpServerPrepareURL = HTTPS_PROTOCOL + host + ":" + HTTP_SERVER_PORT + HTTP_TESTING_RESOURCE + "/prepare"
httpServerprintStatisticsURL =  HTTPS_PROTOCOL + host + ":" + HTTP_SERVER_PORT + HTTP_TESTING_RESOURCE + "/printStatistics"


#PAYLOADS
EMPTY_JSON = {}
httpPreparePaylaod = {'sensorPrototypeName':'huminity',"messageIntervalTime":messageIntervalTime}
def printAvg():
    with open('temperature_AccuracyEvaluation,json') as json_file:
        data = json.load(json_file)
        print("Avg delay time:", data['avgSensorMessageDelay'])




#Main

sensorsNumber = 10

#while sensorsNumber < 100:
prepareResponse = requests.post( url = httpServerPrepareURL, json = httpPreparePaylaod)
httpServerPrepareHttpResponseCode = prepareResponse.status_code 
print(" prepareTesting response:", prepareResponse.text, "and prepareTesting Http Response Code:", httpServerPrepareHttpResponseCode, "\n")



workloadGeneratorRestartResponse = requests.post( url = workloadGeneratorRestartURL, json = EMPTY_JSON)
workloadGeneratorRestartHttpResponseCode = workloadGeneratorRestartResponse.status_code
print(" workloadGeneratorRetart response:", workloadGeneratorRestartResponse.text, "and workloadGeneratorRestart Http Response Code:", workloadGeneratorRestartHttpResponseCode, "\n")
    




#workloadGeneratorStartResponse = requests.post( url = workloadGeneratorStartURL, json = EMPTY_JSON)
#workloadGeneratorStartHttpResponseCode = workloadGeneratorStartResponse.status_code
#print(" workloadGeneratorStart response:", workloadGeneratorStartResponse.text, "and workloadGeneratorStart Http Response Code:", workloadGeneratorStartHttpResponseCode, "\n")
    
time.sleep(waitTime*60)

workloadGeneratorStopResponse = requests.post( url = workloadGeneratorStopURL, json = EMPTY_JSON)
workloadGeneratorStopHttpResponseCode = workloadGeneratorStopResponse.status_code
print(" workloadGeneratorStop response:", workloadGeneratorStopResponse.text, "and workloadGeneratorStop Http Response Code:", workloadGeneratorStopHttpResponseCode, "\n")
 

printStatisticsResponse = requests.post( url = httpServerprintStatisticsURL, json = EMPTY_JSON)
httpServerprintStatisticsHttpResponseCode = printStatisticsResponse.status_code 
print(" printStatistics response:", printStatisticsResponse.text, "and printStatistics Http Response Code:", httpServerprintStatisticsHttpResponseCode, "\n")

resetResponse = requests.post( url = httpServerResetURL, json = EMPTY_JSON)
httpServerResetHttpResponseCode = resetResponse.status_code 
print(" resetTesting response:", resetResponse.text, "and resetTesting Http Response Code:", httpServerResetHttpResponseCode, "\n")


#printAvg()
#include <SoftwareSerial.h>

/*************set bluetooth pin**************/
SoftwareSerial BT(10, 11); // RX | TX
/*****************設定硬體******************/
#define MQ_9_PIN                   (1) // MQ_9 輸入腳
#define MQ_4_PIN                   (2) // MQ_4 輸入腳
#define MQ_4_D0_PIN                (4) // MQ_4 D0輸入腳
#define RL_VALUE                   (5) // RL電阻為 10K歐姆
#define MQ_4_Ro_CLEAN_AIR_FACTOR     (1) //MQ_4在乾淨空氣中的電阻比值
#define MQ_9_Ro_CLEAN_AIR_FACTOR     (1) //MQ_4在乾淨空氣中的電阻比值
/*****************設定軟體******************/
#define CALIBARAION_SAMPLE_TIMES      (50)    //設定校準時樣品讀取次數
#define CALIBRATION_SAMPLE_INTERVAL   (100)   //設定較準時每次讀取採樣的時間間隔(ms)
#define READ_SAMPLE_INTERVAL         (50)    //測試時用幾次的檢測值的平均值
#define READ_SAMPLE_TIMES            (5)    //測試時每次取樣的間隔時間(ms)
/****************全域變數*******************/
float MQ_4_Ro = 16.8;
float MQ_9_Ro = 17.6;
int fartOrNot=0;
int time = millis(); 

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
  //pinMode(MQ_4_D0, INPUT);
  //MQ_4_Ro =MQ_4Calibration(MQ_4_PIN);
  //MQ_9_Ro =MQ_9Calibration(MQ_9_PIN);
  for(int i=0;i<20;i++){
    Serial.print("Warming-up. Please wait.");
    delay(250);
    Serial.print(".");
    delay(250);
    Serial.print(".");
    delay(250);
    Serial.print(".");
    delay(250);
    Serial.println(".");
  }
  
}

void loop() {
  /*{m, b}*/
  /*(log10(ratio)*m + b);*/
  /*ratio = RS_gas / R0;*/
  float MQ_4_Scalar[2] = {-2.81,3.01};
  float MQ_9_Scalar[2] = {-2.14,2.997};
  int mq4d0=digitalRead(MQ_4_D0_PIN);

  int testdata[2]={0,0};
  String datastring="0";
  int now_time = millis();
  
  if((now_time - time) >= 100)     //actives every 100 milliseconds
  {
    testdata[0]=(int)getPPM(MQ_4_PIN,MQ_4_Ro,MQ_4_Scalar);
    testdata[1]=(int)getPPM(MQ_9_PIN,MQ_9_Ro,MQ_9_Scalar);
    if((mq4d0!=HIGH))
    {
    datastring ="1";
    }
    datastring += ';';
    datastring += testdata[0];
    datastring += ';';
    datastring += testdata[1];
    /*Serial.print("MQ4 ro: ");   
    Serial.print(MQ_4_Ro); 
    Serial.print("MQ9 ro: ");   
    Serial.print(MQ_9_Ro); */
    /*Serial.print("MQ4 ratio: ");   
    Serial.print(getRatio(MQ_4_PIN,MQ_4_Ro)); 
    Serial.print("MQ9 ratio: ");   
    Serial.print(getRatio(MQ_9_PIN,MQ_9_Ro)); */
    Serial.print(fartOrNot);   
    if(mq4d0!=HIGH){
      fartOrNot+=1;
      Serial.print("You fart!");   
    }
    Serial.print("................MQ-4: ");   
    Serial.print(testdata[0]); 
    Serial.print(";  MQ-9: ");   
    Serial.println(testdata[1]); 
    Serial.println("****"+datastring); 
       // BT.write(200); //send packet to phone
        BT.println((datastring)); //send packet to phone
        //BT.write((datastring[2])); //send packet to phone
    time = now_time;
  }
  delay(5);
 // delay(1000);
}
float getRatio(int sensorPin,float Ro){
   float sensor_volt; //Define variable for sensor voltage
  float Rs; //Define variable for sensor resistance
  float sensorValue = analogRead(sensorPin); //Read analog values of sensor
  sensor_volt *= (5.0 / 1023.0); //Convert analog values to voltage
  Rs = ((5.0 * 10.0) / sensor_volt) - 10.0; //Get value of RS in a gas
  return Rs / Ro;
 }
float getPPM(int sensorPin,float Ro,float Scalar[]){
  float sensor_volt; //Define variable for sensor voltage
  float Rs; //Define variable for sensor resistance
  float sensorValue = analogRead(sensorPin); //Read analog values of sensor
  sensor_volt = sensorValue * (5.0 / 1023.0); //Convert analog values to voltage
  Rs = ((5.0 * 10.0) / sensor_volt) - 10.0; //Get value of RS in a gas
  
  return pow(10,log10(Rs / Ro)*Scalar[0]+Scalar[1]);
}
float MQ_4Calibration(int MQ_4_pin)
{
  float sensor_volt; //Define variable for sensor voltage
  float RS_air; //Define variable for sensor resistance
  float R0; //Define variable for R0
  int i;
  float sensor_val=0;
  for (i=0;i<CALIBARAION_SAMPLE_TIMES;i++) {           
    sensor_val += analogRead(MQ_4_pin);
    delay(CALIBRATION_SAMPLE_INTERVAL);
  }
  sensor_val = sensor_val/CALIBARAION_SAMPLE_TIMES;
  sensor_volt = sensor_val* (5.0 / 1023.0); //Convert average to voltage
  RS_air = ((5.0 * 10.0) / sensor_volt) - 10.0; //Calculate RS in fresh air
  R0 = RS_air / 4.4; //Calculate R0
  return R0;
}
float MQ_9Calibration(int MQ_9_pin)
{
  float sensor_volt; //Define variable for sensor voltage
  float RS_air; //Define variable for sensor resistance
  float R0; //Define variable for R0
  int i;
  float sensor_val=0;
  for (i=0;i<CALIBARAION_SAMPLE_TIMES;i++) {           
    sensor_val += analogRead(MQ_9_pin);
    delay(CALIBRATION_SAMPLE_INTERVAL);
  }
  sensor_val = sensor_val/CALIBARAION_SAMPLE_TIMES;
  sensor_volt = sensor_val* (5.0 / 1023.0); //Convert average to voltage
  RS_air = ((5.0 * 10.0) / sensor_volt) - 10.0; //Calculate RS in fresh air
  R0 = RS_air / 4.4; //Calculate R0
  return R0;//
}

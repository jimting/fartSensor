# fartSensor
for2017IotClass

----------

目前版本2018/01/11

----------

Android端

1.可以連接FB資訊

2.完整的藍芽連接模組

3.完整的歷史紀錄顯示模組

4.完整的臭屁偵測模組

----------

後端

1.使用者註冊

2.設備註冊

3.拿取使用者歷史列表

4.拿取特定歷史資料

5.新增新資料

6.在test/example/裡有輸出成折線圖的php檔案

7.DB結構:
			1.user	 = 	user_id(Varchar)
			2.device = 	device_id(int/ai)	|user_id(Varchar)	|device_mac(Varchar)
			3.history=	history_id(int/ai)	|user_id(Varchar)	|device_id(int)		|result(Varchar)	|resultURL(Varchar)	|date(Datetime)

----------

硬體端

finalProject.ino

為Arduino的Code檔，包含資料處理、藍芽推播與濃度換算

----------

1/12Demo、1/11結案囉～

Programming by JT & 桓慶
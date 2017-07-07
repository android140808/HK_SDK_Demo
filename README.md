#LF02A
1.	按键短于2s 接口，主动通知手机端短按的事件
2.	按键 2s 到 15s 接口，主动通知手机端长按事件
3.	长按20s 接口，主动通知手机端长按事件，然后关机（手环设备）
请看示例代码：
AppsBluetoothManager.getInstance(this).setPressSecondListener(new PressSecondListener() {
            @Override
            public void shortpresseconds(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 2s 短按 命令 == " + action);
            }

            @Override
            public void longpressseconds_2_15s(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 2s-15s 长按 命令 == " + action);
            }

            @Override
            public void longpressseconds_20(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 20s 长按 命令 == " + action);
            }
        });
4.	灯开关控制，其中有颜色的等有颜色控制。
示例（控制表盘上的 7,8,9,10 点的灯亮，以及控制三色灯为绿色）：
二进制码：				 100011110000000
转换码（十六进制）：   47 80
在项目中要通过LEDBean来设置控制的灯光，然后通过LEDControlSwitch来实现即可。
代码：
/**
*   示例（控制表盘上的 7,8,9,10 点的灯亮，以及控制三色灯为绿色）
 *   47 80
 *   100011110000000
 */
                LEDBean bean = new LEDBean();
                bean.setFirst_8_bytes((byte) 0x47);
                bean.setLast_8_bytes((byte) 0x80);
                byte[] content = bean.getContent();
                if (on4) {
                    on4 = false;
                } else {
                    on4 = true;
                    content = bean.getSetOffCommand();//获取关闭LED灯的命令
                }
                LEDControlSwitch led = new LEDControlSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置LED模式成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置LED模式失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, content);
                AppsBluetoothManager.getInstance(this).sendCommand(led);

5.	紫外线监控开关控制。默认为关
byte state = -1;
                if (on1) {
                    state = AvatertSwitch.SET_ON;
                    on1 = false;
                } else {
                    state = AvatertSwitch.SET_OFF;
                    on1 = true;
                }
                AvatertSwitch a1 = new AvatertSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置紫外线成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置紫外线失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, AvatertSwitch.UVV_SWITCH, state);
                AppsBluetoothManager.getInstance(this).sendCommand(a1);
6.	睡眠监测和计步功能开关
7 OTA 升级DEMO
整个项目demo在GitHub中已经开源，请异步到：
https://github.com/android140808/HK_SDK_Demo.git

登录请用默认账号，否则无法注册，请知悉。

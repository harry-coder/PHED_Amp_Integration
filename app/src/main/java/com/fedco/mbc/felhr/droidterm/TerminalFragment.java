package com.fedco.mbc.felhr.droidterm;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.felhr.connectivityservices.UsbService;
import com.fedco.mbc.felhr.constant.Command;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.IOnToggleKeyboard;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import com.fedco.mbc.felhr.encodings.Cp437;
import com.fedco.mbc.felhr.usbserial.UsbSerialDebugger;
import com.fedco.mbc.felhr.vtterminal.EmulatorView;
import com.fedco.mbc.felhr.vtterminal.TermKeyListener;
import com.fedco.mbc.logging.Log;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalFragment extends Fragment {
    private static final Logger TAG = Logger.getLogger("TerminalFragment.java");
    public static final int BLACK = -16777216;
    public static final int BLUE = -13349187;
    private static final int[][] COLOR_SCHEMES = new int[][]{new int[]{-16777216, -1}, new int[]{-1, -16777216}, new int[]{-1, BLUE}, new int[]{-1, BLUE}};
    public static final int DROIDTERM_BACKGROUND_COLOR = 463222;
    private static final String ENCODING_MODE = "input_mode";
    private static final String ENCODING_PREFERENCES = "input_preferences";
    private static final String INPUT_MODE = "input_mode";
    private static final String INPUT_PREFERENCES = "input_preferences";
    public static final int SOFTKEYBOARD_HIDE = 0;
    public static final int SOFTKEYBOARD_SHOW = 1;
    public static final int WHITE = -1;
    private ITerminalCommunicator comm;
    private IOnToggleKeyboard comm2;
    private Cp437 cp437;
    private EmulatorView emulatorView;
    private int encodingMode;
    private StringBuilder hexBuffer;
    private StringBuilder BUFFER = new StringBuilder();
    private boolean hexMode;
    private InputMethodManager im;
    private int inputMode;
    private boolean keyboardON;
    private int mColorId = 3;
    private int mFontSize = 14;
    private Handler mHandler;
    private RelativeLayout mainLayout;
    private Bundle savedData;
    private SharedPreferences sharedPreferences;
    private Button eloyyedButton;
    private Button secureButton;
    private TextView LogBOX;
    private View view;
    private static final String DEVICE_ELOYEED = "DEVICE_ELOYEED";
    private static final String DEVICE_LNT = "DEVICE_LNT";
    private static final String DEVICE_GENUS = "DEVICE_GENUS";
    private static final String DEVICE_SECURE = "DEVICE_SECURE";
    private static final String DEVICE_PALMOHAN = "DEVICE_PALMOHAN";
    private static final String DEVICE_GENUS_OPT = "DEVICE_GENUS_OPT";
    private static final String DEVICE_LPR_54382 = "DEVICE_LPR_54382";
    private static final String DEVICE_LPR_43580 = "DEVICE_LPR_43580";
    private static final String DEVICE_LPR_09682 = "DEVICE_LPR_09682";
    private static final String DEVICE_VISIONTEK = "DEVICE_VISIONTEK";
    private static final String DEVICE_MONTEL = "DEVICE_MONTEL";
    private static final String DEVICE_AVON = "DEVICE_AVON";
    private static final String DEVICE_BENTEK = "DEVICE_BENTEK";
    private static final String DEVICE_AVON_OPT = "DEVICE_AVON_OPT";
    private static final String DEVICE_ALLOYED_OPT = "DEVICE_ALLOYED_OPT";
    private static final String DEVICE_AVON_3PH = "DEVICE_AVON_3PH";
    private static final String DEVICE_DLMS = "DEVICE_DLMS";
    private static final String DEVICE_VISIONTEK_6LOWPAN = "DEVICE_VISIONTEK_6LOWPAN";
    private boolean FLAG = false;
    byte[] buffer = new byte[1024];
    private int command_count = 0, command_retry_count = 1;
    private boolean isFinish = false;
    private static byte[] usbReceivedRawData = UsbService.rawData;
    private static byte[] poll;
    ProgressDialog progress;
    List<Byte> array = new ArrayList<Byte>();
    List<String> array2 = new ArrayList<String>();
    public static int visionTeachFlag = 0;
    //    private static String response_log;
    public static boolean isGenusOptical = false;
    public static boolean isMontel = false;
    private boolean isMeterNumberResponse = false, isSendFFFFFFEnergyCommand;
    public static boolean isAvonResponse = false;
    public static boolean isBentek = false;
    public static boolean isAvonOpt = false;
    public static boolean isAlloyedOpt = false;
    public static boolean isAvon3Ph = false;
    public static boolean isDlms = false;
    public static boolean isVisiontek6LowPan = false, isGenusOpticalDivisibleCommandSend = false;
    private int genus_optical_meter_number, bentekMeterNumber, avon3PhMeterNumber, genus_optical_dividend_position;
    private String dlms_meter_number, dlms_meter_energy_value, dlms_meter_md_value, meter_number;
    private boolean isDLMSEnergyValue, isMeterNumber, isGenusOptEnergy, isDLMSEnergyHexFound, isDLMSMDHexFound;
    private double genusOpticalEnergyValue;
    private String dlms_energy_value_hex = "", dlms_md_value_hex = "", genus_optical_meter_response_energy_hex;
    private boolean isVisionTekSecondCommandSent;


    public interface ITerminalCommunicator {
        void write(byte[] bArr);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.terminal_layout, container, false);
        eloyyedButton = (Button) view.findViewById(R.id.btn_device_elloyed);
        secureButton = (Button) view.findViewById(R.id.btn_device_secure);
        this.mainLayout = (RelativeLayout) view;
        this.im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (MainActivity) getActivity();
        this.comm2 = (IOnToggleKeyboard) getActivity();
        this.hexBuffer = new StringBuilder();
        this.hexMode = false;
        this.emulatorView = (EmulatorView) getActivity().findViewById(R.id.emulatorView);
        this.emulatorView.initialize(this);
        this.emulatorView.setFocusable(true);
        this.emulatorView.setFocusableInTouchMode(true);
        this.emulatorView.requestFocus();
        this.emulatorView.register(new TermKeyListener());
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.emulatorView.setTextSize((int) (((float) this.mFontSize) * metrics.density));
        this.emulatorView.setColors(COLOR_SCHEMES[this.mColorId][0], Color.rgb(7, 17, 118));
        this.sharedPreferences = getActivity().getSharedPreferences("input_preferences", Context.MODE_PRIVATE);
        this.inputMode = this.sharedPreferences.getInt("input_mode", 0);
        this.sharedPreferences = getActivity().getSharedPreferences("input_preferences", Context.MODE_PRIVATE);
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            this.encodingMode = this.sharedPreferences.getInt("input_mode", 1);
        } else {
            this.encodingMode = this.sharedPreferences.getInt("input_mode", 1);
        }
        this.cp437 = new Cp437();
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        this.emulatorView.setLayout(this.mainLayout);
        this.emulatorView.onResume();
        this.savedData = null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.emulatorView.updateSize();
    }

    public void onPause() {
        super.onPause();
        if (this.emulatorView != null) {
            this.im.hideSoftInputFromWindow(this.emulatorView.getWindowToken(), 0);
            this.emulatorView.onPause();
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (this.progress != null) {
            this.progress.dismiss();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(MainActivity.CL_KEY, getText());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void write(byte[] buffer) {
        Log.i(TAG, "Written command  : \n" + HexData.hexToString(buffer));

        if (this.inputMode == 1 && buffer[0] == (byte) 10) {
            buffer[0] = (byte) 13;
            this.comm.write(buffer);
        } else if (this.inputMode == 2 && buffer[0] == (byte) 10) {
            this.comm.write(new byte[]{(byte) 10, (byte) 13});
        } else {
            this.comm.write(buffer);
        }
    }

    public void writeWithEncoding(int ch) {
        encodingMode = 1;
        if (this.inputMode == 1 && ch == 10) {
            this.comm.write(new byte[]{(byte) 13});
        } else if (this.inputMode == 2 && ch == 10) {
            this.comm.write(new byte[]{(byte) 10, (byte) 13});
        } else if (this.encodingMode == 1) {
            encodeAndSendChar(ch, "ISO-8859-1");
        } else if (this.encodingMode == 2) {
            encodeAndSendChar(ch, "UTF-16");
        } else if (this.encodingMode == 3) {
            encodeAndSendChar(ch, UsbSerialDebugger.ENCODING);
        } else if (this.encodingMode == 4) {
            if (this.cp437.isCharPresent(ch) != -1) {
                this.comm.write(new byte[]{(byte) this.cp437.isCharPresent(ch)});
            }
        } else {
            encodeAndSendChar(ch, "US-ASCII");
        }
//                encodeAndSendChar(ch, "ISO-8859-1");
    }

    private void encodeAndSendChar(int ch, String encoding) {
        try {
            this.comm.write(new String(new int[]{ch}, 0, 1).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void configureEncoding(int encodingMode) {
        this.encodingMode = 1;
    }

    public void toggleKeyboard() {
        this.comm2.requestFocus();
        this.im.toggleSoftInput(2, 0);
    }

    public void closeKeyboard() {
        this.im.toggleSoftInput(1, 0);
    }

    public void printText(String text) {

        if (this.hexMode) {
            this.hexBuffer.append(text);
        } else {
            this.hexBuffer.append(HexData.hexToString(text.getBytes()));
        }

//        response_log += text.toString().trim();
        Command.RESPONSE += text.toString().trim();

        BUFFER = new StringBuilder();
        BUFFER.append(HexData.hexToString(text.getBytes()));

        this.emulatorView.write(text.getBytes(), text.getBytes().length);
    }

    public void printText(byte[] data) {
        this.hexBuffer.append(HexData.hexToString(data));
        this.emulatorView.write(data, data.length);
        usbReceivedRawData = data;

    }

//    public void printByte(byte[] rawResponse) {
//
//    }

    public void showHexValues() {
        this.hexMode = !this.hexMode;
        String hexBufferStr = this.hexBuffer.toString();
        this.emulatorView.resetHardTerminal();
        this.emulatorView.write(hexBufferStr.getBytes(), hexBufferStr.getBytes().length);
    }

    public void showString() {
        this.hexMode = !this.hexMode;
        byte[] data = HexData.stringTobytes(this.hexBuffer.toString());
        this.emulatorView.resetHardTerminal();
        String currentString = hextToString(data);
        this.emulatorView.write(currentString.getBytes(), currentString.getBytes().length);
    }

    public String hextToString(byte[] data) {

        encodingMode = 1;
        try {
            if (this.encodingMode == 0) {
                return new String(data, "US-ASCII");
            }
            if (this.encodingMode == 1) {
                return new String(data, "ISO-8859-1");
            }
            if (this.encodingMode == 2) {
                return new String(data, "UTF-16");
            }
            if (this.encodingMode == 3) {
                return new String(data, UsbSerialDebugger.ENCODING);
            }
            if (this.encodingMode == 4) {
                int[] codepoints = new int[data.length];
                int i = 0;
                for (byte b : data) {
                    codepoints[i] = this.cp437.lookup(b);
                    i++;
                }
                return new String(codepoints, 0, codepoints.length);
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearTerminal() {
        this.hexBuffer.setLength(0);
        this.hexBuffer.trimToSize();
        this.emulatorView.resetHardTerminal();
    }

//    public void softClearTerminal() {
//    }

    public String getText() {
        return this.emulatorView.getTranscriptText();
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setInput(int id) {
        switch (id) {
            case 0:
                this.inputMode = 0;
                return;
            case 1:
                this.inputMode = 1;
                return;
            case 2:
                this.inputMode = 2;
                return;
            default:
                return;
        }
    }

    /*------ For Elloyed Meter Read -------*/
    public void elloyedClick() {
        resetAllFlags("");
        // this function is for elloyd meter
        write(Command.Eloyeed.ENERGY_VALUE);
        new WaitFor2Seconds(2000).execute(DEVICE_ELOYEED);

    }

    /*------ For LnT Meter Read -------*/
    public void LnTClick() {
        resetAllFlags("");
        Log.i(TAG, "==================LnT INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        write(Command.LnT.ATTENTION);
        new WaitFor2Seconds(2000).execute(DEVICE_LNT);
    }

    /*------ For Secure Meter Read -------*/
    public void secureClick() {
        resetAllFlags("");
        // this function is for elloyd meter
        write(Command.Secure.SECURE_VALUE);
        new WaitFor2Seconds(2000).execute(DEVICE_SECURE);

    }

    /*------ For Genus Meter Read -------*/
    public void genusClick() {
        resetAllFlags("");
        Log.i(TAG, "==================GENUS INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        write(Command.Genus.command1);
        new WaitFor2Seconds(2000).execute(DEVICE_GENUS);

    }

    /*------ For Genus OPT Meter Read -------*/
    public void genusOPTClick() {
        resetAllFlags("isGenusOptical");
        Log.i(TAG, "==================GENUS OPTICAL INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        write(Command.Genus_Opt.command1);
        new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
    }

    /*------ For PalMohan Meter Read -------*/
    public void palmohanClick() {
        resetAllFlags("");
        write(Command.Palmohan.PALMOHAN_ENERGY_VALUE);
        new WaitFor2Seconds(1000).execute(DEVICE_PALMOHAN);

    }

    /*------ For lpr54382 Meter Read -------*/
    public void lpr54382Click() {
        resetAllFlags("");
        write(Command.LPR54382.command1);
        new WaitFor2Seconds(1000).execute(DEVICE_LPR_54382);

    }

    /*------ For lpr43580 Meter Read -------*/
    public void lpr43580Click() {
        resetAllFlags("");
        write(Command.LPR43580.command1);
        new WaitFor2Seconds(1000).execute(DEVICE_LPR_43580);

    }

    /*------ For lpr09682 Meter Read -------*/
    public void lpr09682Click() {
        resetAllFlags("");
        write(Command.LPR09682.command1);
        new WaitFor2Seconds(1000).execute(DEVICE_LPR_09682);

    }

    /*------ For Genus OPT Meter Read -------*/
    public void visionTekClick() {
        resetAllFlags("");
        Log.i(TAG, "==================VISIONTEK_SinglePhase_Optical_IRDA INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        visionTeachFlag = 0;
        write(Command.VISIONTEK.command1);
        new WaitFor2Seconds(2000).execute(DEVICE_VISIONTEK);
    }

    //    /*------ For Montel Meter Read -------*/

    public void montelClick() {
        resetAllFlags("isMontel");
        Log.i(TAG, "==================MONTEL INITIATE==================");
        command_count = 1;
        command_retry_count = 1;

        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);

        write(Command.MONTEL.command_meter_number);
        new WaitFor2Seconds(1000).execute(DEVICE_MONTEL);

    }

    public void avonClick() {
        resetAllFlags("isAvonResponse");
        Log.i(TAG, "==================AVON INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.AVON.command_authentication);
        new WaitFor2Seconds(1000).execute(DEVICE_AVON);

    }

    public void bentekClick() {
        resetAllFlags("isBentek");
        Log.i(TAG, "==================BEKTEK INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";

        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.BENTEK.meterNumberCommand);
        new WaitFor2Seconds(5000).execute(DEVICE_BENTEK);

    }

    public void avonOptClick() {
        resetAllFlags("isAvonOpt");
        Log.i(TAG, "==================AVON OPTICAL INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.AVON_OPT.command_AllData);
        new WaitFor2Seconds(1000).execute(DEVICE_AVON_OPT);

    }

    public void alloyedOptClick() {
        resetAllFlags("isAlloyedOpt");
        Log.i(TAG, "==================ALLOYED OPTICAL INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.ALLOYED_OPT.command_AllData);
        new WaitFor2Seconds(1000).execute(DEVICE_ALLOYED_OPT);
    }

    public void avon3PhClick() {
        resetAllFlags("isAvon3Ph");
        Log.i(TAG, "==================AVON 3PH INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.AVON_3PH.meterNumberCommand);
        new WaitFor2Seconds(3000).execute(DEVICE_AVON_3PH);
    }

    public void dlmsClick() {
        resetAllFlags("isDlms");
        Log.i(TAG, "==================DLMS INITIATE==================");

        write(Command.DLMS.DISCONNECT_COMMAND);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                command_count = 1;
                command_retry_count = 1;
                Command.RESPONSE = "";
                UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                UsbService.stringBuffer.setLength(0);

                write(Command.DLMS.SNRM);
                new WaitFor2Seconds(500).execute(DEVICE_DLMS);
            }
        }.execute();


    }

    public void visiontek6LowPanClick() {
        resetAllFlags("isVisiontek6LowPan");
        Log.i(TAG, "==================Visiontek 6LowPan INITIATE==================");
        command_count = 1;
        command_retry_count = 1;
        Command.RESPONSE = "";
        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
        UsbService.stringBuffer.setLength(0);
        write(Command.VISIONTEK_6LOWPAN.METER_NUMBER_COMMAND);
        new WaitFor2Seconds(3000).execute(DEVICE_VISIONTEK_6LOWPAN);
    }

    /*------ 2 sec hold Asynctask to Execute COMMAND  -------*/

    class WaitFor2Seconds extends AsyncTask<String, Void, String> {

        int COMMAND_WAITING_TIMING;

        public WaitFor2Seconds(int COMMAND_WAITING_TIMING) {
            this.COMMAND_WAITING_TIMING = COMMAND_WAITING_TIMING;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                Thread.sleep(COMMAND_WAITING_TIMING); //Response waiting time COMMAND_WAITING_TIMING seconds

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase(DEVICE_LNT)) {
                parseLnTData();
            } else if (s.equalsIgnoreCase(DEVICE_ELOYEED)) {
                parseEloyeedData();
            } else if (s.equalsIgnoreCase(DEVICE_GENUS)) {
                parseGenusData();
            } else if (s.equalsIgnoreCase(DEVICE_SECURE)) {
                parseSecureData();
            } else if (s.equalsIgnoreCase(DEVICE_PALMOHAN)) {
                parsePalmohanData();
            } else if (s.equalsIgnoreCase(DEVICE_GENUS_OPT)) {
                parseGenusOPTData();
            } else if (s.equalsIgnoreCase(DEVICE_LPR_54382)) {
                parseLPR54382Data();
            } else if (s.equalsIgnoreCase(DEVICE_LPR_43580)) {
                parseLPR43580Data();
            } else if (s.equalsIgnoreCase(DEVICE_LPR_09682)) {
                parseLPR09682Data();
            } else if (s.equalsIgnoreCase(DEVICE_VISIONTEK)) {
                parseVISIONTEK_SinglePhase_Optical_IRDA();
            } else if (s.equalsIgnoreCase(DEVICE_MONTEL)) {
                parseMontelData();
            } else if (s.equalsIgnoreCase(DEVICE_AVON)) {
                parseAvonData();
            } else if (s.equalsIgnoreCase(DEVICE_BENTEK)) {
                parseBentekData();
            } else if (s.equalsIgnoreCase(DEVICE_AVON_OPT)) {
                parseAvonOptData();
            } else if (s.equalsIgnoreCase(DEVICE_ALLOYED_OPT)) {
                parseAlloyedOptData();
            } else if (s.equalsIgnoreCase(DEVICE_AVON_3PH)) {
                parseAvon3PHData();
            } else if (s.equalsIgnoreCase(DEVICE_DLMS)) {
                parseDlmsData();
            } else if (s.equalsIgnoreCase(DEVICE_VISIONTEK_6LOWPAN)) {
                parseVisiontek6LowPanData();
            }
        }
    }

    private void parseEloyeedData() {
        // Log.i("TerminalFragment","Response: "+Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("")) {
            try {
                String str1 = Command.RESPONSE.split("\\)")[1];
                Log.i(TAG, "First String response :" + str1);

                String str2 = (str1.split("\\)")[0]).substring(3);
                Log.i(TAG, "Second String Response : " + str2);


                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", str2);
                intent.putExtra("MD", "");
                intent.putExtra("meter_number", "");
                getActivity().setResult(2, intent);
                getActivity().finish();

            } catch (Exception e) {
                ((TextView) view.findViewById(R.id.tv_response)).setText("Invalid response format");//wonet reflect in UI hidden

                GSBilling.getInstance().setCodeMetre(0);
                Intent intentsecure = new Intent();
                intentsecure.putExtra("Data", "Meter Not Responding");
                getActivity().setResult(2, intentsecure);
                getActivity().finish();//finishing activity

            }
        } else {

            GSBilling.getInstance().setCodeMetre(0);
            Intent intentsecure = new Intent();
            intentsecure.putExtra("Data", "Meter Not Responding");
            getActivity().setResult(2, intentsecure);
            getActivity().finish();//finishing activity

        }
        Command.RESPONSE = "";
    }

    private void parseSecureData() {
        Log.i(TAG, "SecureData Response: " + Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("")) {
            try {
                String str1 = Command.RESPONSE.split("\\=")[1];
                // String str2 = (str1.split("\\)")[0]).substring(2);

//                String result = String.valueOf();
                String result = String.format("%.2f", Float.parseFloat(str1) / 1.999259189641923);
                ((TextView) view.findViewById(R.id.tv_response)).setText(str1);


                if (this.progress != null) {
                    this.progress.dismiss();
                }


                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", result);
                intent.putExtra("MD", "");
                intent.putExtra("meter_number", "");
                getActivity().setResult(2, intent);
                getActivity().finish();

            } catch (Exception e) {
//                ((TextView) view.findViewById(R.id.tv_response)).setText("Invalid response format");


                if (this.progress != null) {
                    this.progress.dismiss();
                }
                GSBilling.getInstance().setCodeMetre(0);
                Intent intentsecure = new Intent();
                intentsecure.putExtra("Data", "Meter Not Responding");

                getActivity().setResult(2, intentsecure);
                getActivity().finish();//finishing activity


            }
        } else {

            if (this.progress != null) {
                this.progress.dismiss();
            }
            GSBilling.getInstance().setCodeMetre(0);
            Intent intentsecure = new Intent();
            intentsecure.putExtra("Data", "Meter Not Responding");

            getActivity().setResult(2, intentsecure);
            getActivity().finish();//finishing activity

        }
        Command.RESPONSE = "";
    }

    private void parseLnTData() {
        Log.i(TAG, "LnT Response: " + Command.RESPONSE);
        if (Command.RESPONSE.startsWith("/L")) {
            command_retry_count = 1; //reset retry command counter

            /**
             * Send next command
             */
            write(Command.LnT.AUTHENTICATION);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            write(Command.LnT.DATA);
            new WaitFor2Seconds((35 * 1000)).execute(DEVICE_LNT);


        } else if (Command.RESPONSE.startsWith("H") && Command.RESPONSE.contains("!")) {
            command_retry_count = 1; //reset retry command counter
            Log.i(TAG, "LnT Response: " + Command.RESPONSE);

            try {
                String command_response1 = Command.RESPONSE.substring(Command.RESPONSE.indexOf("JI") + 3);
                String command_response2 = command_response1.substring(command_response1.indexOf("JI") + 3);
                command_response2 = command_response2.substring(0, command_response1.indexOf(")"));

                String[] value1 = command_response2.split("00");
                Log.i(TAG, "VALUE 1 : " + value1);

                float sum = 0f;
                for (int cnt = 1; cnt < value1.length; cnt++) {
                    sum = Float.parseFloat(value1[cnt]) + sum;

                }

                /**
                 * Energy value
                 */
                String energy_value = String.valueOf(sum);
                Log.i(TAG, "energy_value : " + energy_value);

                /**
                 * Meter number
                 */
                String meter_number = Command.RESPONSE.substring(37, 45).toUpperCase();
                Log.i(TAG, "meter_number : " + meter_number);

                /**
                 * MD Value
                 */
                String md_value_response = Command.RESPONSE.substring(Command.RESPONSE.indexOf(")L(") + 1);
                String md_value = md_value_response.substring(2, 8);
                Log.i(TAG, "md_value : " + md_value);


                String meterNumber = Command.RESPONSE.substring(37, 45);
                Log.i(TAG, "LNT Meter Number : " + meterNumber);

                String strMD = Command.RESPONSE.substring(Command.RESPONSE.indexOf(")L(") + 1);
                Log.i(TAG, "strMD : " + strMD);

                String lntMDValue = strMD.substring(2, 8);
                Log.i(TAG, "LNT MD Value : " + lntMDValue);


                GSBilling.getInstance().setReadData(sum);

                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", energy_value);
                intent.putExtra("MD", "" + md_value + "");
                intent.putExtra("meter_number", meter_number);
                getActivity().setResult(2, intent);
                getActivity().finish();//finishing activity

                isFinish = true;
                Log.i(TAG, "==================LnT END==================");

            } catch (Exception e) {
                Command.RESPONSE = "";
                write(Command.LnT.ATTENTION);
                new WaitFor2Seconds(2000).execute(DEVICE_LNT);
                Log.i(TAG, "LnT_command_retry_count : " + command_retry_count);
                command_retry_count += 1;
            }

        } else {
            if (command_retry_count <= 3 && !isFinish) {
                Log.i(TAG, "LnT_command_retry_count : " + command_retry_count);
                Command.RESPONSE = "";
                write(Command.LnT.ATTENTION);
                new WaitFor2Seconds(2000).execute(DEVICE_LNT);
                command_retry_count += 1;
            } else {
                if (!isFinish) {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================LnT END==================");
                }
            }
        }
        //-------------------------------------------------------------
        Command.RESPONSE = "";
    }

    private void parseGenusData() {
        try {
            Log.i(TAG, "GENUS Command Response: " + Command.RESPONSE);
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage());
        }

        if (Command.RESPONSE.contains("OSGIv1")) {
            command_count = 2;
            Command.RESPONSE = "";
            write(Command.Genus.command2);
            new WaitFor2Seconds(2000).execute(DEVICE_GENUS);

        } else if (Command.RESPONSE.contains("T1")) {
            command_count = 3;
            Command.RESPONSE = "";
            write(Command.Genus.command3);
            new WaitFor2Seconds(2000).execute(DEVICE_GENUS);
            FLAG = true;

        } else if (Command.RESPONSE.length() == 0 && command_count == 3) {
            command_count = 4;
            write(Command.Palmohan.PALMOHAN_ENERGY_VALUE);
            new WaitFor2Seconds(2000).execute(DEVICE_GENUS);


        } else if (Command.RESPONSE.contains("000")) {
            command_count = 5;
            String str1 = Command.RESPONSE.substring(2, 8);
            float response = Float.valueOf(str1);
            Log.i(TAG, "Energy Value : " + Command.RESPONSE);
            Log.i(TAG, "Float Energy Value : " + response);


            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", String.valueOf(response));
                intent.putExtra("MD", "");
                intent.putExtra("meter_number", "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                isFinish = true;
                Log.i(TAG, "==================GENUS END==================");
            } catch (Exception e) {
                Log.i(TAG, e.getLocalizedMessage());
            }

        } else {
            if (command_retry_count <= 4 && command_count != 5) {
                write(Command.Genus.command1);
                new WaitFor2Seconds(2000).execute(DEVICE_GENUS);
                Log.i(TAG, "Genus Command Retry Count : " + command_retry_count);
                command_retry_count += 1;

            } else {
                try {
                    if (!isFinish) {
                        GSBilling.getInstance().setCodeMetre(0);
                        Intent intentsecure = new Intent();
                        intentsecure.putExtra("Data", "Meter Not Responding");
                        getActivity().setResult(2, intentsecure);
                        getActivity().finish();//finishing activity
                        isFinish = true;
                        Log.i(TAG, "==================GENUS END==================");
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getLocalizedMessage());

                }

            }

        }

        Command.RESPONSE = "";
    }

    //-------------- WOKING GENUS OPT ----------------//
    private void parseGenusOPTData() {
        try {
            Log.i(TAG, "Genus Optical Response : \n" + UsbService.genusOpticalStringBuffer);
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage());
        }
        Log.i(TAG, "Genus Optical Command Response: " + Command.RESPONSE);

        if (Command.RESPONSE.contains("GENUSE")) {
            String str[] = UsbService.genusOpticalStringBuffer.toString().split(" ");
            String value1 = (str[10].replace("0x", "").trim());
            String value2 = (str[11].replace("0x", "").trim());
            String value3 = (str[12].replace("0x", "").trim());
            String value4 = (str[13].replace("0x", "").trim());
            Log.i(TAG, "value1:value2:value3:value4 : " + value1 + "," + value2 + "," + value3 + "," + value4);

            Command.RESPONSE = "";
            UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
            UsbService.genusOpticalStringBuffer.setLength(0);
            isMeterNumberResponse = false;

            write(GetChallengeResponse(value1, value2, value3, value4));
            new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);

        } else if (UsbService.genusOpticalStringBuffer.length() != 0 && UsbService.genusOpticalStringBuffer.toString().contains("0x06")
                && UsbService.genusOpticalStringBuffer.length() < 11) {
            Command.RESPONSE = "";
            UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
            UsbService.genusOpticalStringBuffer.setLength(0);

            write(Command.Genus_Opt.GET_METER_NUMBER_COMMAND);
            new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
            isMeterNumberResponse = true;

        } else if (!isMeterNumberResponse && Command.RESPONSE.length() > 1
                && !Command.RESPONSE.startsWith("?G") && isGenusOptEnergy) {

            /**
             * get energy hex value
             */
            if (isGenusOpticalDivisibleCommandSend) {
                String str[] = UsbService.genusOpticalStringBuffer.toString().split(" ");
                String dividend_hex = str[str.length - 4].replace("0x", "").trim();
                try {
//                    genus_optical_dividend_position = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(dividend_hex)));
//                    Log.i(TAG, "dividend_position : " + genus_optical_dividend_position + " dividend_hex : " + dividend_hex + " at position : " + (str.length - 4));

//                    genusOpticalEnergyValue = ((double) Integer.parseInt(genus_optical_meter_response_energy_hex, 16) / genus_optical_dividend_position);
                    genusOpticalEnergyValue = ((double) Integer.parseInt(genus_optical_meter_response_energy_hex, 16) / 100);
                    Log.i(TAG, "genus_optical_meter_energy_value : " + genusOpticalEnergyValue);

                    Command.RESPONSE = "";
                    UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                    UsbService.genusOpticalStringBuffer.setLength(0);
                    write(Command.Genus_Opt.MD_COMMAND);
                    new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                    isGenusOptEnergy = false;
                    isGenusOpticalDivisibleCommandSend = false;
                } catch (Exception e) {
                    Log.i(TAG, e.getLocalizedMessage());
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Problem to read meter.");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    Log.i(TAG, "==================GENUS OPTICAL END==================");
                    isGenusOptical = false;
                    isFinish = true;
                }
            } else {
                String str[] = UsbService.genusOpticalStringBuffer.toString().split(" ");
                genus_optical_meter_response_energy_hex = (str[4] + str[5] + str[6]).replace("0x", "").trim();
                Log.i(TAG, "genus_optical_meter_response_energy_hex : " + genus_optical_meter_response_energy_hex);

                if (genus_optical_meter_response_energy_hex.equalsIgnoreCase("FFFFFF")) {
                    Command.RESPONSE = "";
                    UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                    UsbService.genusOpticalStringBuffer.setLength(0);
                    write(Command.Genus_Opt.second_energy_command);
                    new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                    isSendFFFFFFEnergyCommand = true;
                } else {
                    if (isSendFFFFFFEnergyCommand) {
                        genus_optical_meter_response_energy_hex = (str[10] + str[11] + str[12]).replace("0x", "").trim();
                        Log.i(TAG, "genus_optical_meter_response_energy_hex : " + genus_optical_meter_response_energy_hex);

                        Command.RESPONSE = "";
                        UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                        UsbService.genusOpticalStringBuffer.setLength(0);
                        write(Command.Genus_Opt.divisible_command);
                        isGenusOpticalDivisibleCommandSend = true;
                        new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);

                    } else {
//                        String dividend_hex = str[str.length - 3].replace("0x", "").trim();
//                        genus_optical_dividend_position = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(dividend_hex)));
//                        Log.i(TAG, "genus_optical_dividend_position : " + genus_optical_dividend_position + " dividend_hex : " + dividend_hex + " at position : " + (str.length - 3));
//                        genusOpticalEnergyValue = ((double) Integer.parseInt(genus_optical_meter_response_energy_hex, 16) / genus_optical_dividend_position);
                        genusOpticalEnergyValue = ((double) Integer.parseInt(genus_optical_meter_response_energy_hex, 16) / 10);
                        Log.i(TAG, "genus_optical_meter_energy_value : " + genusOpticalEnergyValue);

                        Command.RESPONSE = "";
                        UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                        UsbService.genusOpticalStringBuffer.setLength(0);
                        write(Command.Genus_Opt.MD_COMMAND);
                        new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                        isGenusOptEnergy = false;
                    }
                }
            }
        } else if (!Command.RESPONSE.equalsIgnoreCase("")
                && !isMeterNumberResponse
                && UsbService.genusOpticalStringBuffer.length() > 25
                && !isGenusOptEnergy) {
            String str[] = UsbService.genusOpticalStringBuffer.toString().split(" ");
            String meter_response_md = (str[4] + str[5]).replace("0x", "").trim();
            Log.i(TAG, "meter_response_md : " + meter_response_md);

            double genus_optical_meter_md_value;
            if (meter_response_md.equalsIgnoreCase("FFFF")) {
                genus_optical_meter_md_value = 00.00;
            } else {
//                genus_optical_meter_md_value = ((double) Integer.parseInt(meter_response_md, 16) / genus_optical_dividend_position);
                genus_optical_meter_md_value = ((double) Integer.parseInt(meter_response_md, 16) / 1000);
            }
            Log.i(TAG, "genus_optical_meter_md_value : " + genus_optical_meter_md_value);

            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + genusOpticalEnergyValue + "");
                intent.putExtra("MD", "" + genus_optical_meter_md_value + "");
                intent.putExtra("meter_number", "" + genus_optical_meter_number + "");
                getActivity().setResult(2, intent);
                getActivity().finish();//finishing activity
                isFinish = true;

                Log.i(TAG, "==================GENUS OPTICAL END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (isMeterNumberResponse) {
            try {
                String str[] = UsbService.genusOpticalStringBuffer.toString().split(" ");
                String meter_response_meter_number = (str[5] + str[6] + str[7]).replace("0x", "").trim();
                Log.i(TAG, "meter_response_meter_number : " + meter_response_meter_number);

                genus_optical_meter_number = Integer.parseInt(meter_response_meter_number, 16);
                Log.i(TAG, "genus_optical_meter_number : " + genus_optical_meter_number);

                Command.RESPONSE = "";
                UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                UsbService.genusOpticalStringBuffer.setLength(0);

                write(Command.Genus_Opt.ENERGY_COMMAND);
                new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                isGenusOptEnergy = true;
                isMeterNumberResponse = false;

            } catch (Exception e) {
                Command.RESPONSE = "";
                UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                UsbService.genusOpticalStringBuffer.setLength(0);

                Log.i(TAG, "GENUS OPTICAL COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.Genus_Opt.command1);
                new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                command_retry_count += 1;
                isMeterNumberResponse = false;
            }
        } else {
            if (!isFinish && command_retry_count < 8) {

                Command.RESPONSE = "";
                UsbService.genusOpticalStringBuffer.delete(0, UsbService.genusOpticalStringBuffer.length());
                UsbService.genusOpticalStringBuffer.setLength(0);

                Log.i(TAG, "GENUS OPTICAL COMMAND RETRY COUNT : " + command_retry_count);
                if (command_retry_count == 1 && command_retry_count < 4) {
                    write(Command.Genus_Opt.command1);
                    new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                    command_retry_count += 1;
                } else if (command_retry_count == 4 && command_retry_count < 8) {
                    write(Command.Genus_Opt.command2);
                    new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
                    command_retry_count += 1;
                } else {
                    try {
                        GSBilling.getInstance().setCodeMetre(0);
                        Intent intent = new Intent();
                        intent.putExtra("Data", "Meter Not Responding");
                        getActivity().setResult(2, intent);
                        getActivity().finish();//finishing activity
                        Log.i(TAG, "==================GENUS OPTICAL END==================");

                        isGenusOptical = false;
                        isFinish = true;

                    } catch (Exception e) {
                        Log.i(TAG, e.getLocalizedMessage());
                        isGenusOptical = false;
                        isFinish = true;
                        Log.i(TAG, "==================GENUS OPTICAL END==================");
                    }
                }
//                command_retry_count += 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    Log.i(TAG, "==================GENUS OPTICAL END==================");

                    isGenusOptical = false;
                    isFinish = true;

                } catch (Exception e) {
                    Log.i(TAG, e.getLocalizedMessage());
                    isGenusOptical = false;
                    isFinish = true;
                    Log.i(TAG, "==================GENUS OPTICAL END==================");
                }
            }
        }
    }

    //-----------------LPR54382-------------------//

    private void parseLPR54382Data() {

        Log.i(TAG, "LPR54382 Response: " + Command.RESPONSE);
        Log.i(TAG, "LPR54382 Response LENGTH: " + Command.RESPONSE.length());

        String hexChvalue1 = null;
        String hexChvalue2 = null;
        String hexChvalue3 = null;

        if (Command.RESPONSE.contains("MPMKVVCL")) {

            Log.i(TAG, "Termminal Catch of ARG0 COnv : " + HexData.hexToString(UsbService.rawData));

            write(Command.LPR54382.command2);
            new WaitFor2Seconds(1000).execute(DEVICE_LPR_54382);

            Command.RESPONSE = "";

        } else if (Command.RESPONSE.contains("LIPG")) {

            Log.i(TAG, "RES Step 2 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 2 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));
//
//            write(Command.Genus_Opt.command3);
//            new WaitFor2Seconds(1000).execute(DEVICE_GENUS_OPT);
//
//
//            Command.RESPONSE = "";

//            for (int i = 0; i < UsbService.rawData.length; i++) {
            for (int i = 0; i < Command.RESPONSE.length(); i++) {

                char c = Command.RESPONSE.charAt(20);
                char c1 = Command.RESPONSE.charAt(21);
                char c2 = Command.RESPONSE.charAt(22);

//                char c = Command.RESPONSE.charAt(4);
//                char c1 = Command.RESPONSE.charAt(0);
//                char c2 = Command.RESPONSE.charAt(0);
                hexChvalue1 = String.format("%02x", (int) c);
                hexChvalue2 = String.format("%02x", (int) c1);
                hexChvalue3 = String.format("%02x", (int) c2);


            }

            Log.i(TAG, "" + hexChvalue1);
            Log.i(TAG, "" + hexChvalue2);
            Log.i(TAG, "" + hexChvalue3);

//            String[] fnlARG= HexData.hexToString(UsbService.rawData);

            Log.i(TAG, "RES Step Length : " + UsbService.rawData.length);
            Log.i(TAG, "RES Step 3 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 3 ARG - HEX : " + HexData.hexToString(UsbService.rawData));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()).length());
//            Log.i(TAG, "ENERGY VALUE GENUS  : " + ProcessEnergydataLPR(hexChvalue3, hexChvalue2, hexChvalue1));

//            LogBOX.append("\nCommand.RESPOND: "+HexData.hexToString(Command.RESPONSE.getBytes())+"\n"+
//                    "ARG 0 : "+HexData.hexToString(UsbService.rawData) +"\n"+
//                    "First Bytes : "+ "00"+"\n"+
//                    "Second Bytes : "+ "00"+"\n"+
//                    "Third Bytes : "+ hexChvalue3+"\n"+
//                    "ENERGY VALUE GENUS  : " +ProcessEnergydata("00", "00", hexChvalue3));

//            Toast.makeText(getActivity(), "ARG 0 : "+HexData.hexToString(UsbService.rawData), Toast.LENGTH_LONG).show();
//            Toast.makeText(getActivity(), "COM RES : "+HexData.hexToString(Command.RESPONSE.getBytes()), Toast.LENGTH_LONG).show();

//
            GSBilling.getInstance().setCodeMetre(1);
            Intent intent = new Intent();
            intent.putExtra("Data", String.valueOf(ProcessEnergydataLPR(hexChvalue3, hexChvalue2, hexChvalue1)));
            intent.putExtra("MD", "");
            intent.putExtra("meter_number", "");
            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity


        } else {

            GSBilling.getInstance().setCodeMetre(0);
            Intent intent = new Intent();
            intent.putExtra("Data", "Meter Not Responding");
            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity

        }
    }

    //-----------------LPR43580-------------------//
    private void parseLPR43580Data() {

        Log.i(TAG, "LPR43580 Response: " + Command.RESPONSE);
        Log.i(TAG, "LPR43580 Response LENGTH: " + Command.RESPONSE.length());

        String hexChvalue1 = null;
        String hexChvalue2 = null;
        String hexChvalue3 = null;

        if (Command.RESPONSE.contains("MPMKVVCL")) {

            Log.i(TAG, "Termminal Catch of ARG0 COnv : " + HexData.hexToString(UsbService.rawData));

            write(Command.LPR43580.command2);
            new WaitFor2Seconds(1000).execute(DEVICE_LPR_43580);

            Command.RESPONSE = "";

        } else if (Command.RESPONSE.contains("35TL")) {

            Log.i(TAG, "RES Step 2 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 2 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));

            for (int i = 0; i < Command.RESPONSE.length(); i++) {

                char c = Command.RESPONSE.charAt(20);
                char c1 = Command.RESPONSE.charAt(21);
                char c2 = Command.RESPONSE.charAt(22);

                hexChvalue1 = String.format("%02x", (int) c);
                hexChvalue2 = String.format("%02x", (int) c1);
                hexChvalue3 = String.format("%02x", (int) c2);


            }

            Log.i(TAG, "" + hexChvalue1);
            Log.i(TAG, "" + hexChvalue2);
            Log.i(TAG, "" + hexChvalue3);

//            String[] fnlARG= HexData.hexToString(UsbService.rawData);

            Log.i(TAG, "RES Step Length : " + UsbService.rawData.length);
            Log.i(TAG, "RES Step 3 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 3 ARG - HEX : " + HexData.hexToString(UsbService.rawData));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()).length());


            GSBilling.getInstance().setCodeMetre(1);
            Intent intent = new Intent();
            intent.putExtra("Data", String.valueOf(ProcessEnergydataLPR(hexChvalue3, hexChvalue2, hexChvalue1)));
            intent.putExtra("MD", "");
            intent.putExtra("meter_number", "");
            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity

        } else {

//            GSBilling.getInstance().setCodeMetre(0);
//
//            Intent intent = new Intent();
//            intent.putExtra("Data", "Meter Not Responding");
//
//            getActivity().setResult(2, intent);
//            getActivity().finish();//finishing activity

            GSBilling.getInstance().setCodeMetre(0);

            Intent intent = new Intent();
            intent.putExtra("Data", "Meter Not Responding");

            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity

        }
    }

    /*  --------------LPR09682 ---------------- */
    private void parseLPR09682Data() {

        Log.i(TAG, "LPR09682 Response: " + Command.RESPONSE);
        Log.i(TAG, "LPR09682 Response LENGTH: " + Command.RESPONSE.length());

        String hexChvalue1 = null;
        String hexChvalue2 = null;
        String hexChvalue3 = null;

        if (Command.RESPONSE.contains("MPMKVVCL")) {

            Log.i(TAG, "Termminal Catch of ARG0 COnv : " + HexData.hexToString(UsbService.rawData));

            write(Command.LPR09682.command2);
            new WaitFor2Seconds(1000).execute(DEVICE_LPR_09682);

            Command.RESPONSE = "";

        } else if (Command.RESPONSE.contains("STWL")) {

            Log.i(TAG, "RES Step 2 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 2 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));
            for (int i = 0; i < Command.RESPONSE.length(); i++) {

                char c = Command.RESPONSE.charAt(20);
                char c1 = Command.RESPONSE.charAt(21);
                char c2 = Command.RESPONSE.charAt(22);

                hexChvalue1 = String.format("%02x", (int) c);
                hexChvalue2 = String.format("%02x", (int) c1);
                hexChvalue3 = String.format("%02x", (int) c2);

            }

            Log.i(TAG, "" + hexChvalue1);
            Log.i(TAG, "" + hexChvalue2);
            Log.i(TAG, "" + hexChvalue3);

            Log.i(TAG, "RES Step Length : " + UsbService.rawData.length);
            Log.i(TAG, "RES Step 3 : " + Command.RESPONSE);
            Log.i(TAG, "RES Step 3 ARG - HEX : " + HexData.hexToString(UsbService.rawData));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()));
            Log.i(TAG, "RES Step 3 HEX : " + HexData.hexToString(Command.RESPONSE.getBytes()).length());
//            Log.i(TAG, "ENERGY VALUE GENUS  : " + ProcessEnergydataLPR(hexChvalue3, hexChvalue2, hexChvalue1));


            GSBilling.getInstance().setCodeMetre(1);
            Intent intent = new Intent();
            intent.putExtra("Data", String.valueOf(ProcessEnergydataLPR(hexChvalue3, hexChvalue2, hexChvalue1)));
            intent.putExtra("MD", "");
            intent.putExtra("meter_number", "");
            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity

        } else {

            GSBilling.getInstance().setCodeMetre(0);

            Intent intent = new Intent();
            intent.putExtra("Data", "Meter Not Responding");

            getActivity().setResult(2, intent);
            getActivity().finish();//finishing activity

        }
    }

    private void parsePalmohanData() {
        Log.i(TAG, "Palmohan Response : " + Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("")) {
            try {
                String str1 = Command.RESPONSE.split("\\(")[1];
                Log.i(TAG, "First String response :" + str1);

                String str2 = str1.split("\\)")[0];
                Log.i(TAG, "Second String Response : " + str2);

                ((TextView) view.findViewById(R.id.tv_response)).setText("Energy Value: " + str2);
//

                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", str2);
                intent.putExtra("MD", "");
                intent.putExtra("meter_number", "");
                getActivity().setResult(2, intent);
                getActivity().finish();//finishing activity


            } catch (Exception e) {

                ((TextView) view.findViewById(R.id.tv_response)).setText("Invalid response format");

                GSBilling.getInstance().setCodeMetre(0);
                Intent intentsecure = new Intent();

                intentsecure.putExtra("Data", "Meter Not Responding");

                getActivity().setResult(2, intentsecure);
                getActivity().finish();//finishing activity

            }
        } else {

            GSBilling.getInstance().setCodeMetre(0);
            Intent intentsecure = new Intent();
            intentsecure.putExtra("Data", "Meter Not Responding");

            getActivity().setResult(2, intentsecure);
            getActivity().finish();//finishing activity
        }
        Command.RESPONSE = "";
    }

    //-------------- VISIONTEK_SinglePhase_Optical_IRDA ----------------//

    private void parseVISIONTEK_SinglePhase_Optical_IRDA() {
        try {

            Log.i(TAG, "VISIONTEK_SinglePhase_Optical_IRDA Response : \n" + UsbService.stringBuffer);

        } catch (Exception e) {

        }


        if (visionTeachFlag == 0) {
            visionTeachFlag = 1;
            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(Command.VISIONTEK.command2);
            new WaitFor2Seconds(3000).execute(DEVICE_VISIONTEK);

        } else if (visionTeachFlag == 1) {
            if (Command.RESPONSE.getBytes().length > 50) {
                String str[] = UsbService.stringBuffer.toString().split(" ");
                /**
                 * get meter number
                 */
                String meter_number_hex_value = (str[9] + str[10] + str[11] + str[12] + str[13] + str[14] + str[15] + str[16]).replace("0x", "").trim();
                Log.i(TAG, "meter_number_hex_value : " + meter_number_hex_value);

                try {
                    Integer.parseInt((str[9] + str[10]).replace("0x", "").trim());
                    Integer.parseInt((str[11] + str[12]).replace("0x", "").trim());
                    Integer.parseInt((str[13] + str[14]).replace("0x", "").trim());
                    Integer.parseInt((str[15] + str[16]).replace("0x", "").trim());
                    String meter_number = new String(hexStringToByteArray(meter_number_hex_value), "UTF-8");
                    Log.i(TAG, "meter_number : " + meter_number);
                    /**
                     * energy value
                     */
                    String meter_response_energy;
                    if (isVisionTekSecondCommandSent) {
                        meter_response_energy = (str[47] + str[46] + str[45]).replace("0x", "").trim();
                    } else {
//                        meter_response_energy = (str[45] + str[44] + str[43]).replace("0x", "").trim();
                        meter_response_energy = (str[46] + str[45] + str[44] + str[43]).replace("0x", "").trim();
                    }
                    Log.i(TAG, "meter_response_energy : " + meter_response_energy);
                    double energy_value = ((double) Integer.parseInt(meter_response_energy, 16) / 100);
                    Log.i(TAG, "energy_value : " + energy_value);

                    /**
                     * MD value
                     */
                    String meter_response_md;
                    if (isVisionTekSecondCommandSent) {
                        meter_response_md = (str[50] + str[49]).replace("0x", "").trim();
                    } else {
//                        meter_response_md = (str[48] + str[47]).replace("0x", "").trim();
                        meter_response_md = (str[48] + str[47]).replace("0x", "").trim();
                    }
                    Log.i(TAG, "meter_response_md : " + meter_response_md);
                    double md_value = ((double) Integer.parseInt(meter_response_md, 16) / 1000);
                    Log.i(TAG, "md_value : " + md_value);

                    try {
                        GSBilling.getInstance().setCodeMetre(1);
                        Intent intent = new Intent();
                        intent.putExtra("Data", "" + energy_value + "");
                        intent.putExtra("MD", "" + md_value + "");
                        intent.putExtra("meter_number", meter_number);
                        getActivity().setResult(2, intent);
                        getActivity().finish();//finishing activity
                        isFinish = true;
                        visionTeachFlag = 0;
                    } catch (Exception e) {

                    }

                    Log.i(TAG, "==================VISIONTEK_SinglePhase_Optical_IRDA END==================");
                } catch (Exception e) {
                    /**
                     * if meter number is not valid integer
                     * then try second command
                     */
                    if (!isVisionTekSecondCommandSent) {
                        Command.RESPONSE = "";
                        UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                        UsbService.stringBuffer.setLength(0);
                        isVisionTekSecondCommandSent = true;
                        write(Command.VISIONTEK.SECOND_COMMAND_FOR_METER_READING);
                        new WaitFor2Seconds(3000).execute(DEVICE_VISIONTEK);
                    } else {
                        if (command_retry_count < 4) {
                            visionTeachFlag = 0;
                            Command.RESPONSE = "";
                            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                            UsbService.stringBuffer.setLength(0);
                            isVisionTekSecondCommandSent = false;
                            write(Command.VISIONTEK.command1);
                            new WaitFor2Seconds(2000).execute(DEVICE_VISIONTEK);
                            Log.i(TAG, "VISIONTEK_SinglePhase_Optical_IRDA COMMAND RETRY COUNT : " + command_retry_count);
                            command_retry_count += 1;

                        } else {
                            try {
                                GSBilling.getInstance().setCodeMetre(0);
                                Intent intent = new Intent();
                                intent.putExtra("Data", "Meter Not Responding");
                                getActivity().setResult(2, intent);
                                getActivity().finish();//finishing activity
                                isFinish = true;
                                visionTeachFlag = 0;
                            } catch (Exception ex) {

                            }
                            Log.i(TAG, "==================VISIONTEK_SinglePhase_Optical_IRDA END==================");
                        }
                    }
                }
            } else {

                if (command_retry_count < 4) {
                    visionTeachFlag = 0;
                    Command.RESPONSE = "";
                    UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                    UsbService.stringBuffer.setLength(0);
                    write(Command.VISIONTEK.command1);
                    new WaitFor2Seconds(2000).execute(DEVICE_VISIONTEK);
                    Log.i(TAG, "VISIONTEK_SinglePhase_Optical_IRDA COMMAND RETRY COUNT : " + command_retry_count);
                    command_retry_count += 1;

                } else {
                    try {
                        GSBilling.getInstance().setCodeMetre(0);
                        Intent intent = new Intent();
                        intent.putExtra("Data", "Meter Not Responding");
                        getActivity().setResult(2, intent);
                        getActivity().finish();//finishing activity
                        isFinish = true;
                        visionTeachFlag = 0;
                    } catch (Exception e) {

                    }
                    Log.i(TAG, "==================VISIONTEK_SinglePhase_Optical_IRDA END==================");
                }
            }
        } else {
            if (!isFinish) {
                GSBilling.getInstance().setCodeMetre(0);
                Intent intent = new Intent();
                intent.putExtra("Data", "Meter Not Responding");
                getActivity().setResult(2, intent);
                getActivity().finish();//finishing activity
            }
            Log.i(TAG, "==================VISIONTEK_SinglePhase_Optical_IDAData END==================");
        }
    }

    private void parseMontelData() {
        try {
            Log.i(TAG, "Montel Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Montel Command Response : " + Command.RESPONSE);

        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1) {
            String str[] = UsbService.stringBuffer.toString().split(" ");

            /**
             * Meter number
             */
            meter_number = (str[3] + str[4] + str[5] + str[6]).replace("0x", "").trim();
            Log.i(TAG, "montelMeterNumber : " + meter_number);

            String str2[] = HexData.hexToString(Command.MONTEL.command_energy_md_value).split(" ");
            str2[3] = str[3];
            str2[4] = str[4];
            str2[5] = str[5];
            str2[6] = str[6];

            StringBuilder builder = new StringBuilder();
            for (String s : str2) {
                builder.append(s + " ");
            }
            String secondCommand = builder.toString().trim();
            Log.i(TAG, "secondCommand : " + secondCommand);
            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(HexData.stringTobytes(secondCommand));
            command_count = 2;
            new WaitFor2Seconds(1000).execute(DEVICE_MONTEL);

        } else if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 2) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String meter_energy_value_hex = (str[14] + str[13] + str[12]).replace("0x", "").trim();
            /**
             * Energy value
             */
            double meter_energy_value = ((double) Integer.parseInt(meter_energy_value_hex, 16) / 10);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            /**
             * MD Value
             */
            String meter_md_value_hex = (str[16] + str[15]).replace("0x", "").trim();
            double meter_md_value = ((double) Integer.parseInt(meter_md_value_hex, 16) / 1000);
            Log.i(TAG, "meter_md_value : " + meter_md_value);

            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", meter_number);
                getActivity().setResult(2, intent);
                getActivity().finish();
                isFinish = true;
                Log.i(TAG, "==================MONTEL END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "MONTEL COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.MONTEL.command_meter_number);
                new WaitFor2Seconds(1000).execute(DEVICE_MONTEL);
                command_retry_count += 1;
                command_count = 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================MONTEL END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseAvonData() {
        String checkingByte = "", meter_energy_value_hex = "";
        try {
            Log.i(TAG, "Avon Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Avon Command Response : " + Command.RESPONSE);

        if (!Command.RESPONSE.equalsIgnoreCase("") && UsbService.stringBuffer.length() > 1 && UsbService.stringBuffer.length() < 6) {
            String response = (UsbService.stringBuffer.toString().split(" ")[0]).replace("0x", "").trim().toUpperCase();
            String second_command = HexData.hexToString(Command.AVON.command_get_all_data).replace("0xCC", "0x" + response);
            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(HexData.stringTobytes(second_command));
            new WaitFor2Seconds(2000).execute(DEVICE_AVON);

        } else if (!Command.RESPONSE.equalsIgnoreCase("") && UsbService.stringBuffer.length() > 145) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            checkingByte = str[7].replace("0x", "").trim();
            /**
             * Meter number
             */
            String meter_number_hex = (str[7] + str[8] + str[9]).replace("0x", "").trim();
            int meter_number = Integer.parseInt(meter_number_hex, 16);
            Log.i(TAG, "meter_number : " + meter_number);

//            String meter_energy_value_hex = (str[150]+str[149]+str[148]).replace("0x", "").trim();
            if (checkingByte.equalsIgnoreCase("96")) {
                meter_energy_value_hex = (str[197] + str[196] + str[195] + str[194]).replace("0x", "").trim();
            } else if (checkingByte.equalsIgnoreCase("0F")) {
                meter_energy_value_hex = (str[126] + str[125]).replace("0x", "").trim();
            } else if (checkingByte.equalsIgnoreCase("97")) {
                meter_energy_value_hex = (str[136] + str[135]).replace("0x", "").trim();
            } else {
                meter_energy_value_hex = (str[149] + str[148]).replace("0x", "").trim();
            }

            /**
             * Energy value
             */
            double meter_energy_value = ((double) Integer.parseInt(meter_energy_value_hex, 16) / 100);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);
            /**
             * MD Value
             */
            String meter_md_value_hex = (str[151] + str[152]).replace("0x", "").trim();
            double meter_md_value = ((double) Integer.parseInt(meter_md_value_hex, 16) / 1000);
            Log.i(TAG, "meter_md_value : " + meter_md_value);
            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", "" + meter_number + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================AVON END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "AVON COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.AVON.command_authentication);
                new WaitFor2Seconds(1000).execute(DEVICE_AVON);
                command_retry_count += 1;

            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================AVON END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseBentekData() {
        try {
            Log.i(TAG, "Bentek Meter command USB Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Bentek Command Response : " + Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String meterNumberHex = (str[3] + str[2] + str[1]).replace("0x", "").trim();
            Log.i(TAG, "meterNumberHex : " + meterNumberHex);

            bentekMeterNumber = Integer.parseInt(meterNumberHex, 16);
            Log.i(TAG, "bentekMeterNumber : " + bentekMeterNumber);

            String firstCommandResponse[] = UsbService.stringBuffer.toString().split(" ");
            String secondCommand = HexData.hexToString(Command.BENTEK.bentekCommand2).replace("0x01", firstCommandResponse[1])
                    .replace("0x02", firstCommandResponse[2]).replace("0x03", firstCommandResponse[3]);
            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(HexData.stringTobytes(secondCommand));
            command_count = 2;
            new WaitFor2Seconds(5000).execute(DEVICE_BENTEK);
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 2) {
            write(Command.BENTEK.energy_md_meternumberCommand);
            command_count = 3;
            new WaitFor2Seconds(5000).execute(DEVICE_BENTEK);
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 3) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String energyValueHex1 = (str[150] + str[149] + str[148] + str[147]).replace("0x", "").trim();
//            String energyValueHex2 = (str[113] + str[112] + str[111] + str[110]).replace("0x", "").trim();
            String energyValueHex2 = (/*str[113] + */str[112] + str[111] + str[110] + str[109]).replace("0x", "").trim();
            Log.i(TAG, "energyValueHex1 : " + energyValueHex1);
            Log.i(TAG, "energyValueHex2 : " + energyValueHex2);

            int energyValue1 = Integer.parseInt(energyValueHex1, 16);
            int energyValue2 = Integer.parseInt(energyValueHex2, 16);

            double meter_energy_value = (energyValue1 >= energyValue2) ? energyValue1 : energyValue2;
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            String md_value_hex;
            if (energyValue1 >= energyValue2) {
//                md_value_hex = (str[158] + str[157]).replace("0x", "").trim();
                md_value_hex = (str[157] + str[156]).replace("0x", "").trim();
            } else {
                md_value_hex = (str[121] + str[120]).replace("0x", "").trim();
            }

            Log.i(TAG, "md_value_hex : " + md_value_hex);

            double meter_md_value = ((double) Integer.parseInt(md_value_hex, 16) / 100);
            Log.i(TAG, "meter_md_value : " + meter_md_value);
            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", "" + bentekMeterNumber + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================BENTEK END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "BENTEK COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.BENTEK.meterNumberCommand);
                command_count = 1;
                new WaitFor2Seconds(5000).execute(DEVICE_BENTEK);
                command_retry_count += 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================BENTEK END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseAvonOptData() {
        try {
            Log.i(TAG, "Avon Opt Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Avon Opt Command Response : " + Command.RESPONSE);

        if (!Command.RESPONSE.equalsIgnoreCase("") && UsbService.stringBuffer.length() > 145) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            /**
             * Meter number
             */
            String meter_number_hex = (str[7].replace("0x", "").trim()) + (str[8].replace("0x", "").trim()) + (str[9].replace("0x", "").trim());
            int meter_number = Integer.parseInt(meter_number_hex, 16);
            Log.i(TAG, "meter_number : " + meter_number);

            /**
             * Energy value
             */
            String meter_energy_value_hex = (str[57].replace("0x", "").trim()) + (str[58].replace("0x", "").trim()) + (str[59].replace("0x", "").trim());
            double meter_energy_value = ((double) Integer.parseInt(meter_energy_value_hex, 16) / 100);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            /**
             * MD Value
             */
            String meter_md_value_hex = (str[60].replace("0x", "").trim()) + (str[61].replace("0x", "").trim());
            double meter_md_value = ((double) Integer.parseInt(meter_md_value_hex, 16) / 1000);
            Log.i(TAG, "meter_md_value : " + meter_md_value);
            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", "" + meter_number + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================AVON OPTICAL END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "AVON OPTICAL COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.AVON_OPT.command_AllData);
                new WaitFor2Seconds(1000).execute(DEVICE_AVON_OPT);
                command_retry_count += 1;

            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================AVON OPTICAL END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseAlloyedOptData() {
        try {
            Log.i(TAG, "Alloyed Opt Meter command USB Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Alloyed Opt Command Response : " + Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String meterNumberHex = (str[3] + str[4] + str[5] + str[6]).replace("0x", "").trim();
            Log.i(TAG, "meterNumberHex : " + meterNumberHex);

            int meterNumber = Integer.parseInt(meterNumberHex, 16);
            Log.i(TAG, "meterNumber : " + meterNumber);

            String energyValueHex = (str[46] + str[47] + str[48]).replace("0x", "").trim();
            Log.i(TAG, "energyValueHex : " + energyValueHex);
            /*
            * Energy Value*/
            double meter_energy_value = ((double) Integer.parseInt(energyValueHex, 16) / 1000);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + "0.0" + "");
                intent.putExtra("meter_number", "" + meterNumber + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================ALLOYED OPT END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "ALLOYED COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.ALLOYED_OPT.command_AllData);
                command_count = 1;
                new WaitFor2Seconds(5000).execute(DEVICE_ALLOYED_OPT);
                command_retry_count += 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================ALLOYED OPT END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseAvon3PHData() {
        try {
            Log.i(TAG, "Avon 3PH Meter command USB Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Avon 3PH Meter Command Response : \n" + Command.RESPONSE);
        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String meterNumberHex = (str[3] + str[4] + str[5] + str[6]).replace("0x", "").trim();
            Log.i(TAG, "meterNumberHex : " + meterNumberHex);

            avon3PhMeterNumber = Integer.parseInt(meterNumberHex, 16);
            Log.i(TAG, "meter_number : " + avon3PhMeterNumber);

            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(Command.AVON_3PH.command_AllData);
            command_count = 2;
            new WaitFor2Seconds(1000).execute(DEVICE_AVON_3PH);
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 2) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String energyValueHex = (str[4] + str[5] + str[6] + str[7]).replace("0x", "").trim();
            Log.i(TAG, "energyValueHex : " + energyValueHex);

            double meter_energy_value = ((double) Integer.parseInt(energyValueHex, 16) / 100);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            String md_value_hex = (str[34] + str[35]).replace("0x", "").trim();
            Log.i(TAG, "md_value_hex : " + md_value_hex);

            double meter_md_value = ((double) Integer.parseInt(md_value_hex, 16) / 100);
            Log.i(TAG, "meter_md_value : " + meter_md_value);
            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", "" + avon3PhMeterNumber + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================AVON 3PH END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "AVON 3PH COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.AVON_3PH.meterNumberCommand);
                command_count = 1;
                new WaitFor2Seconds(5000).execute(DEVICE_AVON_3PH);
                command_retry_count += 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================AVON 3PH END==================");
                } catch (Exception e) {

                }
            }
        }
    }

    private void parseDlmsData() {
        String first_byte = "", last_byte = "";
        String string_buffer[] = {""};
        try {
            //Log.i(TAG, "LNT DLMS Meter Command Response : \n" + Command.RESPONSE);
            Log.i(TAG, "DLMS Meter command USB Response : \n" + UsbService.stringBuffer);
            string_buffer = UsbService.stringBuffer.toString().split(" ");
            first_byte = string_buffer[0].replace("0x", "");
            last_byte = string_buffer[string_buffer.length - 1].replace("0x", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1 &&
                first_byte.equalsIgnoreCase("7E") && last_byte.equalsIgnoreCase("7E")) {

            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            switch (GSBilling.getInstance().getMeterCode()) {
                case 17:
                    write(Command.LNT_DLMS.command_AARQ);
                    break;
                case 18:
                    write(Command.GENUS_DLMS.command_AARQ);
                    break;
                case 19:
                    write(Command.EMCO_DLMS.command_AARQ);
                    break;
                case 20:
                    write(Command.AVON_DLMS.command_AARQ);
                    break;
                case 21:
                    write(Command.VISIONTEK_DLMS.command_AARQ);
                    break;
                case 22:
                    write(Command.HPL_DLMS.command_AARQ);
                    break;
                case 23:
                    write(Command.LANDIS_DLMS.command_AARQ);
                    break;
                case 25:
                    write(Command.SECURE_DLMS.command_AARQ);
                    break;
            }
            command_count = 2;
            new WaitFor2Seconds(2000).execute(DEVICE_DLMS);

            /**
             * check authentication
             */
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && (string_buffer.length > 16)
                && first_byte.equalsIgnoreCase("7E")
                && last_byte.equalsIgnoreCase("7E")
                && (command_count == 2)) {

            if (isDLMSEnergyHexFound || isDLMSMDHexFound) {
                Command.RESPONSE = "";
                UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                UsbService.stringBuffer.setLength(0);
                if (!isMeterNumber && !isDLMSEnergyValue) {
                    write(Command.DLMS.METER_NUMBER_COMMAND);
                } else if (isMeterNumber && !isDLMSEnergyValue) {
                    write(Command.DLMS.ENERGY_VALUE_COMMAND);
                } else if (isMeterNumber && isDLMSEnergyValue) {
                    write(Command.DLMS.MD_VALUE_COMMAND);
                }
                command_count = 3;
                new WaitFor2Seconds(2000).execute(DEVICE_DLMS);
            } else {
                String str[] = UsbService.stringBuffer.toString().split(" ");
                Log.i(TAG, "str[28] : " + str[28]);
                if (str[28].equalsIgnoreCase("0x00")) {
                    Command.RESPONSE = "";
                    UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                    UsbService.stringBuffer.setLength(0);
                    if (!isMeterNumber && !isDLMSEnergyValue) {
                        write(Command.DLMS.METER_NUMBER_COMMAND);

                    } else if (isMeterNumber && !isDLMSEnergyValue) {
                        write(Command.DLMS.ENERGY_VALUE_COMMAND);

                    } else if (isMeterNumber && isDLMSEnergyValue) {
                        write(Command.DLMS.MD_VALUE_COMMAND);
                    }
                    command_count = 3;
                    new WaitFor2Seconds(2000).execute(DEVICE_DLMS);
                } else {
                    try {
                        GSBilling.getInstance().setCodeMetre(0);
                        Intent intent = new Intent();
                        intent.putExtra("Data", "Unable to read meter..! Invalid Authentication");
                        getActivity().setResult(2, intent);
                        getActivity().finish();//finishing activity
                        isFinish = true;
                        Log.i(TAG, "==================DLMS METER END==================");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }
                }
            }

            /**
             * get meter number
             */
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && string_buffer.length > 16
                && !isMeterNumber && !isDLMSEnergyValue
                && first_byte.equalsIgnoreCase("7E")
                && last_byte.equalsIgnoreCase("7E") && command_count == 3) {

            dlms_meter_number = getDLMSMeterNumber(DEVICE_DLMS);

        } else if (!Command.RESPONSE.equalsIgnoreCase("") && string_buffer.length > 19
                && isMeterNumber
                && first_byte.equalsIgnoreCase("7E")
                && last_byte.equalsIgnoreCase("7E") && command_count == 3) {

            if (!isDLMSEnergyValue) {
                if (!isDLMSEnergyHexFound) {
                    String str[] = UsbService.stringBuffer.toString().split(" ");
                    int strLength = str.length;
//                    dlms_energy_value_hex = (str[17] + str[18] + str[19]).replace("0x", "");
                    dlms_energy_value_hex = (str[(strLength - 7)] + str[(strLength - 6)] + str[(strLength - 5)] + str[(strLength - 4)]).replace("0x", "");
                    Log.i(TAG, "dlms_energy_value_hex : " + dlms_energy_value_hex);

                    /***
                     * write Energy scalar unit command
                     */
                    Command.RESPONSE = "";
                    UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                    UsbService.stringBuffer.setLength(0);
                    write(Command.DLMS.ENERGY_SCALAR_UNIT);
                    new WaitFor2Seconds(2000).execute(DEVICE_DLMS);
                    isDLMSEnergyHexFound = true;

                } else {
                    dlms_meter_energy_value = getDLMSMeterEnergyValue(dlms_energy_value_hex, DEVICE_DLMS);
                }
            } else {
                if (!isDLMSMDHexFound) {
                    String str[] = UsbService.stringBuffer.toString().split(" ");
                    int strLength = str.length;
//                    dlms_md_value_hex = (str[17] + str[18] + str[19]).replace("0x", "");
                    dlms_md_value_hex = (str[(strLength - 7)] + str[(strLength - 6)] + str[(strLength - 5)] + str[(strLength - 4)]).replace("0x", "");
                    Log.i(TAG, "dlms_md_value_hex : " + dlms_md_value_hex);

                    /***
                     * write MD scalar unit command
                     */
                    Command.RESPONSE = "";
                    UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                    UsbService.stringBuffer.setLength(0);
                    write(Command.DLMS.MD_SCALAR_UNIT);
                    new WaitFor2Seconds(2000).execute(DEVICE_DLMS);
                    isDLMSMDHexFound = true;
                } else {
                    dlms_meter_md_value = getDLMSMeterMDValue(dlms_md_value_hex);
                }
                /**
                 * set all data
                 */
                setDLMSMeterData(dlms_meter_number, dlms_meter_energy_value, dlms_meter_md_value, DEVICE_DLMS);
            }
        } else {
            dlms_meter_md_value = 0.0 + "";
            setDLMSMeterData(dlms_meter_number, dlms_meter_energy_value, dlms_meter_md_value, DEVICE_DLMS);
//            setDLMSMeterNotRespondingData("Meter Not Responding", DEVICE_DLMS);
            write(Command.DLMS.DISCONNECT_COMMAND);
            new WaitFor2Seconds(2000).execute("");
//            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
//                Log.i(TAG, "LT DLMS COMMAND RETRY COUNT : " + command_retry_count);
//                write(Command.LNT_DLMS.command_SNRM);
//                command_count = 1;
//                new WaitFor2Seconds(1000).execute(DEVICE_LNT_DLMS);
//                command_retry_count += 1;
//            } else {
//                try {
//                    GSBilling.getInstance().setCodeMetre(0);
//                    Intent intent = new Intent();
//                    intent.putExtra("Data", "Meter Not Responding");
//                    getActivity().setResult(2, intent);
//                    getActivity().finish();//finishing activity
//                    isFinish = true;
//                    Log.i(TAG, "==================LnT DLMS END==================");
//                } catch (Exception e) {
//
//                }
//            }
        }
    }

    private void parseVisiontek6LowPanData() {
        try {
            Log.i(TAG, "Visiontek 6lowpan command USB Response : \n" + UsbService.stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Visiontek 6Lowpan Meter Command Response : \n" + Command.RESPONSE);

        if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 1 && UsbService.stringBuffer.length() >= 74) {
            String str[] = UsbService.stringBuffer.toString().split(" ");
            String meterNumberHex = (str[4] + str[5] + str[6] + str[7] + str[8] + str[9] + str[10] + str[11] + str[12] + str[13] + str[14]).replace("0x", "").trim();
            Log.i(TAG, "meterNumberHex" + meterNumberHex);

            byte[] bytes = HexData.stringTobytes(meterNumberHex);
            try {
                meter_number = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Command.RESPONSE = "";
            UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
            UsbService.stringBuffer.setLength(0);
            write(Command.VISIONTEK_6LOWPAN.ENERGY_COMMAND);
            command_count = 2;
            new WaitFor2Seconds(1000).execute(DEVICE_VISIONTEK_6LOWPAN);
        } else if (!Command.RESPONSE.equalsIgnoreCase("") && command_count == 2) {
            String str[] = UsbService.stringBuffer.toString().split(" ");

            String energyValueHex = (str[119] + str[120] + str[121] + str[122]).replace("0x", "").trim();
            Log.i(TAG, "energyValuehex" + energyValueHex);

            double meter_energy_value = ((double) Integer.parseInt(energyValueHex, 16) / 1000000);
            Log.i(TAG, "meter_energy_value : " + meter_energy_value);

            String md_value_hex = (str[109] + str[110]).replace("0x", "").trim();
            Log.i(TAG, "energyValuehex" + md_value_hex);

            double meter_md_value = ((double) Integer.parseInt(md_value_hex, 16) / 1000);
            Log.i(TAG, "meter_md_value : " + meter_md_value);

            try {
                GSBilling.getInstance().setCodeMetre(1);
                Intent intent = new Intent();
                intent.putExtra("Data", "" + meter_energy_value + "");
                intent.putExtra("MD", "" + meter_md_value + "");
                intent.putExtra("meter_number", "" + meter_number + "");
                getActivity().setResult(2, intent);
                getActivity().finish();
                Log.i(TAG, "==================VISIONTEK 6LOWPAN END==================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (command_retry_count < 4 && Command.RESPONSE.equalsIgnoreCase("")) {
                Log.i(TAG, "VISIONTEK 6LOWPAN COMMAND RETRY COUNT : " + command_retry_count);
                write(Command.VISIONTEK_6LOWPAN.METER_NUMBER_COMMAND);
                command_count = 1;
                new WaitFor2Seconds(5000).execute(DEVICE_VISIONTEK_6LOWPAN);
                command_retry_count += 1;
            } else {
                try {
                    GSBilling.getInstance().setCodeMetre(0);
                    Intent intent = new Intent();
                    intent.putExtra("Data", "Meter Not Responding");
                    getActivity().setResult(2, intent);
                    getActivity().finish();//finishing activity
                    isFinish = true;
                    Log.i(TAG, "==================VISIONTEK 6LOWPAN END==================");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public double ProcessEnergydataLPR(String byte1, String byte2, String byte3) {
        double energy = 0;

        int a = hex2decimal(byte1);
        int b = hex2decimal(byte2);
        int c = hex2decimal(byte3);

//        energy = Convert.ToInt32("&h" + byte1 + byte2 + byte3);
//        ((byte3+byte2)*(byte)256) + (byte1*65535)

        energy = (c + (b * 256) + (a * 65536));
        energy = energy / 100;

        return energy;
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }

    public static byte[] GetChallengeResponse(String ChByt0, String ChByt1, String ChByt2, String ChByt3) {
        int num1, num2, num3, num4, num5, num6;

        num3 = Integer.parseInt(ChByt0, 16);
        num4 = Integer.parseInt(ChByt1, 16);
        num5 = Integer.parseInt(ChByt2, 16);
        num6 = Integer.parseInt(ChByt3, 16);
        num1 = ((num4 + num5) + num6);
        num2 = ((num3 + num5) + num6);
        num1 = (num5 ^ num1);
        num2 = (num6 ^ num2);
        num1 = (num1 + num3);
        num2 = (num2 + num4);
        num1 = num1 % 256;
        num2 = num2 % 256;
        String str1 = IntToHexString(num1);
        String str2 = IntToHexString(num2);
        str1 = reverse1(str1);
        str2 = reverse1(str2);

        byte b = new BigInteger(str1, 16).byteValue();
        byte c = new BigInteger(str2, 16).byteValue();

        byte[] command2 = new byte[]{0x01, 0x42, 0x45, b, c, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, 0};//69 , 82

        command2[9] = CalCheckSumLoad(command2, 9);
        return command2;

    }

    public static byte CalCheckSumLoad(byte[] nInputArrary, int nPktLen) {
        try {
            byte num2;
            byte num = 0;
            for (int i = 1; i < nPktLen; i++) {
                num = (byte) (num + nInputArrary[i]);
            }

//            Log.i("","PRE: "+num);
            num = (byte) ~num;
//            Log.i("","POST : "+num);

            if (num == 0xff) {
                return 0;
            }
            return ((byte) ((int) (num + 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String IntToHexString(int value) {

        String thisLong = "".format("%x", value);
        return thisLong;
    }

    public static String reverse1(String input) {
        char[] in = input.toCharArray();
        int begin = 0;
        int end = in.length - 1;
        char temp;
        while (end > begin) {
            temp = in[begin];
            in[begin] = in[end];
            in[end] = temp;
            end--;
            begin++;
        }
        return new String(in);
    }

    private byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private void resetAllFlags(String flagName) {
        switch (flagName) {
            case "isGenusOptical":
                isGenusOptical = true;
                isMontel = false;
                isAvonResponse = false;
                isBentek = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isMontel":
                isMontel = true;
                isAvonResponse = false;
                isGenusOptical = false;
                isBentek = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isAvonResponse":
                isAvonResponse = true;
                isMontel = false;
                isGenusOptical = false;
                isBentek = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isBentek":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isAvonOpt":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = true;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isElloyedOpt":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = false;
                isAlloyedOpt = true;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isAvon3Ph":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = true;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            case "isDlms":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = true;
                isVisiontek6LowPan = false;
                break;
            case "isVisiontek6LowPan":
                isAvonResponse = false;
                isBentek = true;
                isMontel = false;
                isGenusOptical = false;
                isAvonOpt = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = true;
                isVisiontek6LowPan = true;
                break;
            case "":
                isAvonOpt = false;
                isMontel = false;
                isGenusOptical = false;
                isAvonResponse = false;
                isBentek = false;
                isAlloyedOpt = false;
                isAvon3Ph = false;
                isDlms = false;
                isVisiontek6LowPan = false;
                break;
            default:
                isGenusOptical = false;
                isMontel = false;
                isAvonResponse = false;
        }
    }

    private String getDLMSMeterNumber(final String DEVICE_NAME) {
        String dlms_meter_number = "";
        String meter_number_hex = "";

        /**
         * Meter number
         */
        String str[] = UsbService.stringBuffer.toString().split(" ");

        int meter_number_length = Integer.parseInt((str[16]).replace("0x", "").trim(), 16);
        //android.util.Log.e(TerminalFragment.class.getSimpleName(),"meter_number_length : "+meter_number_length);
        if (meter_number_length > 0) {
            for (int i = 0; i < meter_number_length; i++) {
                int start_position = 17 + i;
                meter_number_hex += str[start_position];
//                android.util.Log.e(TerminalFragment.class.getSimpleName(), "start_position : " + start_position + " , meter_number_hex : " + meter_number_hex);
                byte[] bytes = HexData.stringTobytes(meter_number_hex);
                try {
                    dlms_meter_number = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            meter_number_hex = (str[17] + str[18] + str[19]).replace("0x", "").trim();
//            android.util.Log.e(TerminalFragment.class.getSimpleName(), " meter_number_hex : " + meter_number_hex);
            int meterNumber = Integer.parseInt(meter_number_hex, 16);
            Log.i(TAG, "meterNumber : " + meterNumber);
            dlms_meter_number = meterNumber + "";
        }
        Log.i(TAG, "dlms_meter_number : " + dlms_meter_number);
        isMeterNumber = true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                /**
                 * Disconnect meter communication
                 */
                write(Command.DLMS.DISCONNECT_COMMAND);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                /**
                 * send dlms request mode command
                 */
                Command.RESPONSE = "";
                UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                UsbService.stringBuffer.setLength(0);
                write(Command.DLMS.SNRM);
                command_count = 1;
                new WaitFor2Seconds(500).execute(DEVICE_NAME);

            }
        }.execute();

        return dlms_meter_number;
    }

    private String getDLMSMeterEnergyValue(String energy_value_hex, final String DEVICE_NAME) {
        double meter_energy_value = 0.0;
        String str[] = UsbService.stringBuffer.toString().split(" ");
        String energy_value_dividend_position_hex = (str[16]).replace("0x", "");
        Log.i(TAG, "energy_value_dividend_position_hex : " + energy_value_dividend_position_hex);

        int energy_value_dividend_position = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(energy_value_dividend_position_hex)));
        Log.i(TAG, "energy_value_dividend_position : " + energy_value_dividend_position);
//        double meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / energy_value_dividend_position);
        switch (GSBilling.getInstance().getMeterCode()) {
            case 17:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 100);
                break;
            case 18:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 1000);
                break;
            case 19:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 100);
                break;
            case 20: // Avon
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 100);
                break;
            case 21: // Visiontek
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 1000000);
                break;
            case 22:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 100);
                break;
            case 23:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 10);
                break;
            case 25:
                meter_energy_value = ((double) Integer.parseInt(energy_value_hex, 16) / 100);
                break;
        }
        Log.i(TAG, "dlsm_meter_energy_value : " + meter_energy_value);
        isDLMSEnergyValue = true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                /**
                 * Disconnect meter communication
                 */
                write(Command.DLMS.DISCONNECT_COMMAND);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                /**
                 * send dlms request mode command
                 */
                Command.RESPONSE = "";
                UsbService.stringBuffer.delete(0, UsbService.stringBuffer.length());
                UsbService.stringBuffer.setLength(0);
                write(Command.DLMS.SNRM);
                command_count = 1;
                new WaitFor2Seconds(1000).execute(DEVICE_NAME);

            }
        }.execute();

        return meter_energy_value + "";
    }

    private String getDLMSMeterMDValue(String md_value_hex) {
        String str[] = UsbService.stringBuffer.toString().split(" ");
        String md_value_dividend_position_hex = (str[16]).replace("0x", "");
        Log.i(TAG, "md_value_dividend_position_hex : " + md_value_dividend_position_hex);

        int md_value_dividend_position = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(md_value_dividend_position_hex)));
        Log.i(TAG, "md_value_dividend_position : " + md_value_dividend_position);


        double meter_md_value = ((double) Integer.parseInt(md_value_hex, 16) / md_value_dividend_position);
        Log.i(TAG, "meter_md_value : " + meter_md_value);

        return meter_md_value + "";
    }

    private void setDLMSMeterData(String meter_number, String energy_value, String md_value, final String device_name) {
        try {
            GSBilling.getInstance().setCodeMetre(1);
            Intent intent = new Intent();
            intent.putExtra("Data", energy_value);
            intent.putExtra("MD", md_value);
            intent.putExtra("meter_number", meter_number);
            getActivity().setResult(2, intent);
            getActivity().finish();
            Log.i(TAG, "==================" + device_name + " END==================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDLMSMeterNotRespondingData(String message, final String device_name) {
        try {
            GSBilling.getInstance().setCodeMetre(0);
            Intent intent = new Intent();
            intent.putExtra("Data", message);
            getActivity().setResult(2, intent);
            getActivity().finish();
            Log.i(TAG, "==================" + device_name + " END==================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
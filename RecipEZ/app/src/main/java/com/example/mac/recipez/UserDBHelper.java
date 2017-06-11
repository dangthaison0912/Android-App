package com.example.mac.recipez;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dexterfung on 6/6/2017.
 */

public class UserDBHelper {

    private static List<String> attributeDisplaySeq;
    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static UserDBHelper userDBHelper;
    private static CognitoUserPool userPool;
    private static String user;
    private static CognitoDevice newDevice;

    private static CognitoUserAttributes attributesChanged;
    private static List<AttributeType> attributesToDelete;

    private static List<ItemToDisplay> currDisplayedItems;
    private static int itemCount;

    private static List<ItemToDisplay> trustedDevices;
    private static int trustedDevicesCount;
    private static List<CognitoDevice> deviceDetails;
    private static CognitoDevice thisDevice;
    private static boolean thisDeviceTrustState;

    private static List<ItemToDisplay> firstTimeLogInDetails;
    private static Map<String, String> firstTimeLogInUserAttributes;
    private static List<String> firstTimeLogInRequiredAttributes;
    private static int firstTimeLogInItemsCount;
    private static Map<String, String> firstTimeLogInUpDatedAttributes;
    private static String firstTimeLoginNewPassword;

    private static final String poolId = "eu-west-2_nLsSbk27w";
    private static final String clientId = "jqh1lb0n7j7fpikhagj7nnl24";
    private static final String clientSecret = "9dvu2m40ktfbqdhr2h71c0uv8c1mpjpqinv8vf99sa6ru4f3k06";
    private static final Regions cognitoRegion = Regions.EU_WEST_2;

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    private static boolean emailVerified;
    private static boolean emailAvailable;

    private static Set<String> currUserAttributes;

    public static void init(Context context) {

        setData();
        Log.e("UserDBHelper", "init");

        if (userDBHelper != null && userPool != null) {
            Log.e("UserDBHelper", "all not null");
            return;
        }

        if (userDBHelper == null) {
            Log.e("UserDBHelper", "userDBHelper null");
            userDBHelper = new UserDBHelper();
        }

        if (userPool == null) {
            Log.e("UserDBHelper", "userPool null");
            userPool = new CognitoUserPool(context, poolId, clientId, clientSecret, cognitoRegion);
        }

        emailVerified = false;
        emailAvailable = false;

        currUserAttributes = new HashSet<String>();
        currDisplayedItems = new ArrayList<ItemToDisplay>();
        trustedDevices = new ArrayList<ItemToDisplay>();
        firstTimeLogInDetails = new ArrayList<ItemToDisplay>();
        firstTimeLogInUpDatedAttributes = new HashMap<String, String>();

        newDevice = null;
        thisDevice = null;
        thisDeviceTrustState = false;

    }

    public static void signOutInit() {
        userDBHelper = null;
        userPool = null;
    }

    public static CognitoUserPool getUserPool() {
        return userPool;
    }

    public static Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    public static Map<String, String> getSignUpFieldsO2C() {
        return signUpFieldsO2C;
    }

    public static List<String> getAttributeDisplaySeq() {
        return attributeDisplaySeq;
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setUserDetails(CognitoUserDetails details) {
        userDetails = details;
        refreshWithSync();
    }

    public static  CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public static String getCurrUser() {
        return user;
    }

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static boolean isEmailVerified() {
        return emailVerified;
    }

    public static boolean isEmailAvailable() {
        return emailAvailable;
    }

    public static void setEmailVerified(boolean emailVerif) {
        emailVerified = emailVerif;
    }

    public static void setEmailAvailable(boolean emailAvail) {
        emailAvailable = emailAvail;
    }

    public static void clearCurrUserAttributes() {
        currUserAttributes.clear();
    }

    public static void addCurrUserattribute(String attribute) {
        currUserAttributes.add(attribute);
    }

    public static List<String> getNewAvailableOptions() {
        List<String> newOption = new ArrayList<String>();
        for(String attribute : attributeDisplaySeq) {
            if(!(currUserAttributes.contains(attribute))) {
                newOption.add(attribute);
            }
        }
        return  newOption;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    public  static  int getItemCount() {
        return itemCount;
    }

    public static int getDevicesCount() {
        return trustedDevicesCount;
    }

    public static int getFirstTimeLogInItemsCount() {
        return  firstTimeLogInItemsCount;
    }

    public  static ItemToDisplay getItemForDisplay(int position) {
        return  currDisplayedItems.get(position);
    }

    public static ItemToDisplay getDeviceForDisplay(int position) {
        if (position >= trustedDevices.size()) {
            return new ItemToDisplay(" ", " ", " ", Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"), 0, null);
        }
        return trustedDevices.get(position);
    }

    public static ItemToDisplay getUserAttributeForFirstLogInCheck(int position) {
        return firstTimeLogInDetails.get(position);
    }

    public static void setUserAttributeForDisplayFirstLogIn(Map<String, String> currAttributes, List<String> requiredAttributes) {
        firstTimeLogInUserAttributes = currAttributes;
        firstTimeLogInRequiredAttributes = requiredAttributes;
        firstTimeLogInUpDatedAttributes = new HashMap<String, String>();
        refreshDisplayItemsForFirstTimeLogin();
    }

    public static void setUserAttributeForFirstTimeLogin(String attributeName, String attributeValue) {
        if (firstTimeLogInUserAttributes ==  null) {
            firstTimeLogInUserAttributes = new HashMap<String, String>();
        }
        firstTimeLogInUserAttributes.put(attributeName, attributeValue);
        firstTimeLogInUpDatedAttributes.put(attributeName, attributeValue);
        refreshDisplayItemsForFirstTimeLogin();
    }

    public static Map<String, String> getUserAttributesForFirstTimeLogin() {
        return firstTimeLogInUpDatedAttributes;
    }

    public static void setPasswordForFirstTimeLogin(String password) {
        firstTimeLoginNewPassword = password;
    }

    public static String getPasswordForFirstTimeLogin() {
        return firstTimeLoginNewPassword;
    }

    private static void refreshDisplayItemsForFirstTimeLogin() {
        firstTimeLogInItemsCount = 0;
        firstTimeLogInDetails = new ArrayList<ItemToDisplay>();

        for(Map.Entry<String, String> attr: firstTimeLogInUserAttributes.entrySet()) {
            if ("email_verified".equals(attr.getKey())) {
                continue;
            }
            String message = "";
            if ((firstTimeLogInRequiredAttributes != null) && (firstTimeLogInRequiredAttributes.contains(attr.getKey()))) {
                message = "Required";
            }
            ItemToDisplay item = new ItemToDisplay(attr.getKey(), attr.getValue(), message, Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
            firstTimeLogInDetails.add(item);
            firstTimeLogInRequiredAttributes.size();
            firstTimeLogInItemsCount++;
        }

        for (String attr: firstTimeLogInRequiredAttributes) {
            if (!firstTimeLogInUserAttributes.containsKey(attr)) {
                ItemToDisplay item = new ItemToDisplay(attr, "", "Required", Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
                firstTimeLogInDetails.add(item);
                firstTimeLogInItemsCount++;
            }
        }
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    public static void setDevicesForDisplay(List<CognitoDevice> devicesList) {
        trustedDevicesCount = 0;
        thisDeviceTrustState = false;
        deviceDetails = devicesList;
        trustedDevices = new ArrayList<ItemToDisplay>();
        for(CognitoDevice device: devicesList) {
            if (thisDevice != null && thisDevice.getDeviceKey().equals(device.getDeviceKey())) {
                thisDeviceTrustState = true;
            } else {
                ItemToDisplay item = new ItemToDisplay("", device.getDeviceName(), device.getCreateDate().toString(), Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
                item.setDataDrawable("checked");
                trustedDevices.add(item);
                trustedDevicesCount++;
            }
        }
    }

    public static CognitoDevice getDeviceDetail(int position) {
        if (position <= trustedDevicesCount) {
            return deviceDetails.get(position);
        } else {
            return null;
        }
    }

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static CognitoDevice getThisDevice() {
        return thisDevice;
    }

    public static void setThisDevice(CognitoDevice device) {
        thisDevice = device;
    }

    public static boolean getThisDeviceTrustState() {
        return thisDeviceTrustState;
    }

    public static void setData() {
        attributeDisplaySeq = new ArrayList<String>();
        attributeDisplaySeq.add("email");

        signUpFieldsC2O = new HashMap<String, String>();
        signUpFieldsC2O.put("Email verified", "email_verified");
        signUpFieldsC2O.put("Name", "name");
        signUpFieldsC2O.put("Email", "email");

        signUpFieldsO2C = new HashMap<String, String>();
        signUpFieldsO2C.put("email_verified", "Email verified");
        signUpFieldsO2C.put("name", "Name");
        signUpFieldsO2C.put("email", "Email");
    }

    private static void refreshWithSync() {
        // This will refresh the current items to display list with the attributes fetched from service
        List<String> tempKeys = new ArrayList<>();
        List<String> tempValues = new ArrayList<>();

        emailVerified = false;

        emailAvailable = false;

        currDisplayedItems = new ArrayList<ItemToDisplay>();
        currUserAttributes.clear();
        itemCount = 0;

        for(Map.Entry<String, String> attr: userDetails.getAttributes().getAttributes().entrySet()) {

            tempKeys.add(attr.getKey());
            tempValues.add(attr.getValue());

            if(attr.getKey().contains("email_verified")) {
                emailVerified = attr.getValue().contains("true");
            }

            if(attr.getKey().equals("email")) {
                emailAvailable = true;
            }
        }

        // Arrange the input attributes per the display sequence
        Set<String> keySet = new HashSet<>(tempKeys);
        for(String det: attributeDisplaySeq) {
            if(keySet.contains(det)) {
                // Adding items to display list in the required sequence

                ItemToDisplay item = new ItemToDisplay(signUpFieldsO2C.get(det), tempValues.get(tempKeys.indexOf(det)), "",
                        Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"),
                        0, null);

                if(det.contains("email")) {
                    if(emailVerified) {
                        item.setDataDrawable("checked");
                        item.setMessageText("Email verified");
                    }
                    else {
                        item.setDataDrawable("not_checked");
                        item.setMessageText("Email not verified");
                        item.setMessageColour(Color.parseColor("#E94700"));
                    }
                }

                currDisplayedItems.add(item);
                currUserAttributes.add(det);
                itemCount++;
            }
        }
    }

    private static void modifyAttribute(String attributeName, String newValue) {
        //

    }

    private static void deleteAttribute(String attributeName) {

    }



}

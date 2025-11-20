# Non-Official Zebra SDK Kotlin Wrapper
Please be aware that this library / application / sample is provided as a community project without any guarantee of support

## Purpose
a kotlin wrapper of EMDK and DataWedge API, build for quick develop demo apps
to simplify demo app development use kotlin language instead of Java

## Version
com.symbol:emdk:11.0.134

## Notice
OCR features needs license


## Export .crt file from apk

set environment path for windows
```
"%USERPROFILE%\AppData\Local\Android\Sdk\build-tools\35.0.0"
```

run following command in terminal
```
apksigner.bat verify --print-certs app\build\outputs\apk\debug\app-debug.apk > debug_certificate.crt
apksigner.bat verify --print-certs app\build\outputs\apk\release\app-release.apk > release_certificate.crt
```

## Whitelist app use StageNow Admin Tool (Windows)

Export Mode > AccessMgr

```
Operation Mode = Single User Without Whitelist
Service Access Action = Allow Caller to Call Service
Service Identifier = delegation_scope_datawedge_config_api
Caller Package Name = com.zebra.zebrakotlindemo
```
## Wrong Info on Zebra Techdoc

https://techdocs.zebra.com/datawedge/15-0/guide/api/enumeratescanners/
```
Wrong: 
com.symbol.datawedge.api.ACTION_ENUMERATEDSCANNERLIST
Right: 
com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS
```

https://techdocs.zebra.com/datawedge/15-0/guide/decoders/#code39

1. Typo:
```
i20f5_enable_marginless_decode [i20f5 => i2of5]
trigger-wakeup [- => _]
decoder_dutch_postal_3S [ 3S => 3s]
Adaptive_Scanning [Adaptive_Scanning => adaptive_scanning] 0 enable 1 disable is wired
Beam_Width [Beam_Width => beam_width]
m_bWeightMetric
```

2. Lack of prefix "decoder_" 
```
code39_enable_marginless_decode
i20f5_enable_marginless_decode
```

3. Duplicated Param Names:
```
decoder_code11_report_check_digit
decoder_upce1_preamble
```




## Tips

1. ENABLE_DATAWEDGE parameter value is Boolean type, not String type.
```
https://techdocs.zebra.com/datawedge/latest/guide/api/enabledatawedge/
```

2. please be aware of the intent category setting for both API calling and result receiving.

```
// please do not set category to the DW API calling intent !!!
// DW API result intent come with category "android.intent.category.DEFAULT"

// Calling:
Intent().apply {
   setAction("com.symbol.datawedge.api.ACTION")
}

Receiving:
IntentFilter().apply {
   addAction("com.symbol.datawedge.api.RESULT_ACTION")
   addCategory("android.intent.category.DEFAULT")
}
```

3. default enabled decoders
```
decoder_aztec
decoder_codabar
decoder_code32
decoder_code39
decoder_code128
decoder_composite_ab
decoder_composite_c
decoder_datamatrix
decoder_ean8
decoder_ean13
decoder_pdf417
decoder_maxicode
decoder_micropdf
decoder_qrcode
decoder_tlc39
decoder_upca
decoder_upce0
```

4. SET_CONFIG API PLUGIN_CONFIG parameter pass array doesn't work

plese set plugin one by one by sending multiple intents

5. EMDK doesn't work, profile manager return null

you must setup EMDK AndroidStudio plugin, and create any empty profile to make it works.
please check use the AndroidStudioMenu/EMDK/ProfileManager

6. result of com.symbol.datawedge.api.RESULT_GET_DATAWEDGE_STATUS not sure uppercase or lowercase

please always check "enabled" and "ENABLED" ignoring case.

7. EMDK error: Failed to connect dependency services

Initializing the EMDKManager in multiple activities incorrectly can lead to conflicts and this generic error message. 

8. EMDK error: Failed to find provider info for com.zebra.devicemanager.zdmcontentprovider

ZDNA Cloud Client or Device Manager not installed ?

9. MX PowerKeyManager TouchPanelMode and TouchPanel have same function ?

TouchPanel from MX 5.0
TouchPanelMode from MX 10.2

## AndroidManifest.xml setup

please set all queries in the same block

1. EMDK
```
<queries>
  <package android:name="com.symbol.emdk.emdkservice"/>
</queries>

<uses-permission android:name="com.symbol.emdk.permission.EMDK" />

<application>
   <uses-library android:name="com.symbol.emdk" android:required="false"/>
</application>
```

2. DataWedge API
```
<queries>
   <package android:name="com.symbol.datawedge" />
   <intent>
      <action android:name="com.symbol.datawedge.api.ACTION" />
   </intent>
</queries>

<uses-permission android:name="com.symbol.datawedge.permission.RECEIVE_SCAN" />
```

3. Zebra Content Provider API
```
<queries>
   <package android:name="com.zebra.zebracontentprovider" />
   <provider android:authorities="oem_info" />
</queries>
<uses-permission android:name="com.zebra.provider.READ" />
```

4. Device Manager API (now called Zebra DNA Cloud)
```
<queries>
   <package android:name="com.zebra.devicemanager"/>
</queries>

<uses-permission android:name="com.zebra.devicemanager.provider.READ_PERMISSION"/>
```

5. Device Admin API
```
<uses-permission android:name="android.permission.BIND_DEVICE_ADMIN" />
```

## URLs

[Zebra Signature Export Tool](https://techdocs.zebra.com/sigtools/)

[DataWedge Control Access](
https://techdocs.zebra.com/datawedge/latest/guide/programmers-guides/secure-intent-apis/)

[Download StageNow Admin Tool](https://www.zebra.com/ap/en/support-downloads/software/mobile-computer-software/stagenow.html)

[SET_CONFIG](https://techdocs.zebra.com/datawedge/15-0/guide/api/setconfig/)

[Barcode Decoders](https://techdocs.zebra.com/datawedge/15-0/guide/decoders/)

[Zebra MX Managers](https://techdocs.zebra.com/emdk-for-android/14-0/guide/profile-manager-guides/)
/*
 *    Copyright 2018-2019 Prebid.org, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.prebid.mobile.prebidkotlindemo

import android.app.Application
import android.util.Log
import com.applovin.sdk.AppLovinSdk
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.prebidkotlindemo.utils.Settings
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

class CustomApplication : Application() {

    companion object {
        private const val TAG = "PrebidCustomApplication"
    }

    override fun onCreate() {
        super.onCreate()
        initPrebidSDK()
        initAdMob()
        initApplovinMax()
        TargetingParams.setSubjectToGDPR(true)
        Settings.init(this)
    }

    private fun initPrebidSDK() {
        PrebidMobile.setPrebidServerAccountId("0689a263-318d-448b-a3d4-b02e8a709d9d")
        PrebidMobile.setPrebidServerHost(Host.createCustomHost("https://prebid-server-test-j.prebid.org/openrtb2/auction"))
        PrebidMobile.initializeSdk(applicationContext, object : SdkInitializationListener {
            override fun onSdkInit() {
                Log.d(TAG, "SDK initialized successfully!")
            }

            override fun onSdkFailedToInit(error: InitError?) {
                Log.e(TAG, "SDK initialization error: " + error?.error)
            }
        })
        PrebidMobile.setShareGeoLocation(true)
    }

    private fun initAdMob() {
        MobileAds.initialize(this) {
            Log.d("MobileAds", "Initialization complete.")
        }
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(
            listOf("38250D98D8E3A07A2C03CD3552013B29")
        ).build()
        MobileAds.setRequestConfiguration(configuration)
    }

    private fun initApplovinMax() {
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk { }
        AppLovinSdk.getInstance(this).settings.setVerboseLogging(false)
    }

}

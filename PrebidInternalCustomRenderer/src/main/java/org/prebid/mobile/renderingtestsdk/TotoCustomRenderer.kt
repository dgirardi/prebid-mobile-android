/*
 *    Copyright 2018-2021 Prebid.org, Inc.
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

package org.prebid.mobile.renderingtestsdk

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.exceptions.AdException.THIRD_PARTY
import org.prebid.mobile.api.rendering.customrenderer.CustomBannerRenderer
import org.prebid.mobile.api.rendering.customrenderer.CustomInterstitialRenderer
import org.prebid.mobile.api.rendering.customrenderer.InterstitialControllerInterface
import org.prebid.mobile.configuration.AdUnitConfiguration
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse
import org.prebid.mobile.rendering.bidding.interfaces.InterstitialControllerListener
import org.prebid.mobile.rendering.bidding.listeners.DisplayViewListener

class TotoCustomRenderer : CustomBannerRenderer, CustomInterstitialRenderer {
    override fun getName(): String {
        return "TotoCustomRenderer"
    }

    override fun getVersion(): String {
        return "a-1"
    }

    override fun getToken(): String? {
        return null
    }

    override fun getInterstitialController(
        context: Context?,
        listener: InterstitialControllerListener?
    ): InterstitialControllerInterface {
        return object : InterstitialControllerInterface {
            override fun loadAd(
                adUnitConfiguration: AdUnitConfiguration?,
                bidResponse: BidResponse?
            ) {
                listener?.onInterstitialReadyForDisplay()
                Log.d("TotoCustomRenderer", "loadAd")
            }

            override fun show() {
                Log.d("TotoCustomRenderer", "show")
                listener?.onInterstitialDisplayed();
            }

            override fun destroy() {
                Log.d("TotoCustomRenderer", "destroy")
                listener?.onInterstitialClosed()
            }

        }
    }

    override fun getBannerAdView(
        context: Context,
        listener: DisplayViewListener?,
        adUnitConfiguration: AdUnitConfiguration,
        response: BidResponse
    ): View {
        Log.d("TotoCustomRenderer", "getBannerAdView")

        listener?.onAdLoaded()
        listener?.onAdDisplayed()
        listener?.onAdClicked()
        listener?.onAdClosed()
        listener?.onAdFailed(AdException(THIRD_PARTY, "onAdFailed"))

        val fakeAdView = FrameLayout(context).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            it.setBackgroundColor(Color.parseColor("#ff000000"))
        }

        return fakeAdView
    }
}
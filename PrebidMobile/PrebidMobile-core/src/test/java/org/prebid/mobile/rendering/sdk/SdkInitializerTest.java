package org.prebid.mobile.rendering.sdk;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;
import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.prebid.mobile.Host;
import org.prebid.mobile.PrebidMobile;
import org.prebid.mobile.api.exceptions.InitError;
import org.prebid.mobile.rendering.listeners.SdkInitializationListener;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
public class SdkInitializerTest {

    private boolean calledAlready = false;
    private Boolean isSuccessful;
    private String error;

    private MockWebServer server;
    private Context context;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        context = Robolectric.buildActivity(Activity.class).create().get();
        reset();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
        reset();
    }

    private void reset() {
        calledAlready = false;
        isSuccessful = null;
        error = null;
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(""));
        PrebidContextHolder.clearContext();
    }


    @Test
    public void init_putNullContextAndNullListener_initializationFail() {
        SdkInitializer.init(null, null);

        assertFalse(PrebidMobile.isSdkInitialized());
    }

    @Test
    public void init_putNullContext_initializationFail() {
        SdkInitializer.init(null, createListener());

        assertFalse(isSuccessful);
        assertFalse(PrebidMobile.isSdkInitialized());
        assertEquals(error, "Context must be not null!");
    }

    @Test
    public void init_statusResponseIsOk_initializationIsSuccessful() throws IOException, InterruptedException {
        String host = setResponseStatusAndGetMockServerHostUrl("ok");
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(host));

        SdkInitializer.init(context, createListener());

        sleep(300);
        shadowOf(getMainLooper()).idle();
        sleep(200);

        assertTrue(isSuccessful);
        assertTrue(PrebidMobile.isSdkInitialized());
        assertNull(error);
    }


    private SdkInitializationListener createListener() {
        return new SdkInitializationListener() {

            @Override
            public void onSdkInit() {
                if (calledAlready) fail();

                isSuccessful = true;

                calledAlready = true;
            }

            @Override
            public void onSdkFailedToInit(InitError initError) {
                if (calledAlready) fail();

                isSuccessful = false;
                error = initError.getError();

                calledAlready = true;
            }
        };
    }

    private String setResponseStatusAndGetMockServerHostUrl(String status) throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);
        mockResponse.setBody("{\n    \"application\": {\n        \"status\": \"" + status + "\"\n    }\n}");
        server.enqueue(mockResponse);
        server.start();
        HttpUrl url = server.url("/status");
        server.setProtocolNegotiationEnabled(false);

        return url.toString().replace("/status", "/openrtb2/auction");
    }

}
package com.basics.ming.traineta;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.basics.ming.traineta.dummy.Routes;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @SmallTest
    public void testMethod()
    {
        GetServiceData data = new GetServiceData(this.getContext());

        try {
            ArrayList<Routes> r = data.GetRoutes();
        }
        catch(Exception ex)
        {
            ex.getMessage();
        }
    }

    @SmallTest
    public void testMethod2()
    {

    }

}
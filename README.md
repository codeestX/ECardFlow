#ECardFlow

ECardFlow, A custom ViewPager for multiple card flow system.designed by [Leo Leung](https://ios.uplabs.com/posts/multiple-card-flow
)  
ECardFlow, 一个用于复数卡片滑动与展开效果的ViewPager控件，设计原型出自[Leo Leung](https://ios.uplabs.com/posts/multiple-card-flow
)

#Preview
![](https://github.com/codeestX/ECardFlow/raw/master/preview/GIF.gif)

#Usage

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
   
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.codeestX:ECardFlow:v1.0.0'
	}
	
Step 3. Just use it as a ViewPager

	<moe.codeest.ecardflow.ECardFlow
	        android:id="@+id/ecardflow"
	        android:layout_width="280dp"
	        android:layout_height="match_parent"
	        app:expandMode="slide_up"
	        app:expandTime="700"
	        app:switchSpeed="6"
	        app:preloadPageNum="3"
	        app:maxRotateY="5">

* Only support fragment as each page
* For best performance, giving ECardFlow's parent view an attr`android:clipChildren="false"`,and do `NOT` set ECardFlow's width as`match_parent`
* See DemoProject for more details

Attrs:

	<declare-styleable name="attr">
        <!-- Page switching speed, more big,more slowly, default 6 -->
        <attr name="switchSpeed" format="integer" />

        <!-- Page expand time, default 700ms -->
        <attr name="expandTime" format="integer" />

        <!-- Page expand mode, click or slide up to expand, default slide_up -->
        <attr name="expandMode">
            <enum name="slide_up" value="10" />
            <enum name="click" value="11" />
        </attr>

        <!-- The number of pages outside the screen, default 3 -->
        <attr name="preloadPageNum" format="integer" />

        <!-- The shortest sliding distance to trigger the switch, default context.getScaledTouchSlop() -->
        <attr name="touchSlop" format="integer" />

        <!-- The maximum angle of rotation, default 5 -->
        <attr name="maxRotateY" format="integer" />
    </declare-styleable>

Java Code:

	mEcardflow.gotoNext();
	
	mEcardflow.gotoLast();
	
	mEcardflow.expand();
	
	mEcardflow.shrink();
	
	mEcardflow.setSwitchSpeed(rate);
	
	mEcardflow.setTouchSlop(slop);
	
	mEcardflow.setExpandTime(time);
	
	mEcardflow.setOnExpandStateListener(new ECardFlow.OnExpandStateListener() {
	            @Override
	            public void onExpand(View page, int position) {
	           
	            }
	
	            @Override
	            public void onShrink(View page, int position) {
	          
	            }
	        });

#License

      MIT Copyright (c) 2017 codeestX

#ECardFlow

[![](https://jitpack.io/v/codeestX/ECardFlow.svg)](https://jitpack.io/#codeestX/ECardFlow)

ECardFlow, A custom ViewPager for multiple card flow system.designed by [Leo Leung](https://ios.uplabs.com/posts/multiple-card-flow
)  
ECardFlow, 一个用于复数卡片滑动与展开效果的ViewPager控件，设计原型出自[Leo Leung](https://ios.uplabs.com/posts/multiple-card-flow
)  

ECardFlowLayout, A layout provide beautiful background effect for ViewPager.  
ECardFlowLayout, 一个为ViewPager提供多种联动背景效果的布局

#Preview
<b>ECardFlow:</b>  

![](https://github.com/codeestX/ECardFlow/raw/master/preview/GIF.gif)  

<b>ECardFlowLayout:</b>  

![](https://github.com/codeestX/ECardFlow/raw/master/preview/GIFBlur.gif)
![](https://github.com/codeestX/ECardFlow/raw/master/preview/GIFMove.gif)

#Usage(ECardFlow)

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
   
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.codeestX:ECardFlow:v1.0.5'
	}
	
Step 3. Just use it as a ViewPager

	<moe.codeest.ecardflow.ECardFlow
	        android:id="@+id/ecardflow"
	        android:layout_width="280dp"
	        android:layout_height="match_parent"
	        app:expandMode="slide_up"
	        app:expandTime="700"
	        app:switchTime="1200"
	        app:preloadPageNum="3"
	        app:maxRotateY="5">

* Only support fragment as each page
* For best performance, giving ECardFlow's parent view an attr`android:clipChildren="false"`,and do `NOT` set ECardFlow's width as`match_parent`
* See DemoProject for more details

Attrs:

	<declare-styleable name="attr">
        <!-- Page switching time, default 1200ms -->
        <attr name="switchTime" format="integer" />

        <!-- Page expanding time, default 700ms -->
        <attr name="expandTime" format="integer" />

        <!-- Page expanding mode, click or slide up to expand, default slide_up -->
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
	
	mEcardflow.isExpanding();
	
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
	        
#Usage(ECardFlowLayout)
Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
   
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.codeestX:ECardFlow:v1.0.5'
	}
	
Step 3. Just put a ViewPager in ECardFlowLayout 

	<moe.codeest.ecardflow.ECardFlowLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    android:id="@+id/ecardflow_layout"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:clipChildren="false">
	    <android.support.v4.view.ViewPager
	        android:id="@+id/viewpager"
	        android:layout_width="240dp"
	        android:layout_height="270dp"
	        android:layout_gravity="center_horizontal|bottom"
	        android:layout_marginBottom="80dp"/>
	</moe.codeest.ecardflow.ECardFlowLayout>

Step 4. setAnimMode & setImageProvider

	mVPLayout = (ECardFlowLayout) findViewById(R.id.ecardflow_layout);
	mVPLayout.setAnimMode(new BlurAnimMode());
	mVPLayout.setImageProvider(new DrawableImageProvider(this, res, width, height);

* Set AnimMode before Set ImageProvider
* Use `mVPLayout.onDestroy();` to release source
* See DemoProject for more details

Attrs:  

	<declare-styleable name="attr_layout">
        <!-- The duration of Page switching animation, default 300ms -->
        <attr name="switchAnimTime" format="integer" />
    </declare-styleable>
    
Java Code:

		mVPLayout.setImageProvider(imageProvider);
		
		mVPLayout.setAnimMode(animMode);
		
		mVPLayout.setSwitchAnimTime(time);
		
		//maxSize = Runtime.getRuntime().maxMemory();
		//default: maxSize / 5
		//min: maxSize / 8
		mVPLayout.setCacheSize(megabytes);
		
		mVPLayout.onDestroy();
	
    
| AnimMode      | Des           |
|:-------------:|:-------------:|
| DefaultAnimMode  | No Effect   |
| BlurAnimMode | Blur Effect     |
| MoveAnimMode | Parallax Effect |
| ScaleAnimMode | Scale Effect |
| CrossMoveAnimMode | Cross Parallax Effect |
You can custom AnimMode with class which implements `AnimMode`

| ImageProvider      | Des           |
|:-------------:|:-------------:|
| DrawableImageProvider  | Prividing resId(R.drawable/mipmap.xx) as image resource |
| PathImageProvider | Prividing file path as image resource     |
| FileImageProvider | Prividing `File` as image resource |
You can custom ImageProvider with class which implements `ImageProvider`
  
If you choose `BlurAnimMode`, you can enable `RenderScript` for a high performance.
> Open the build.gradle file in the app folder of your application module.  
> Add the following RenderScript settings to the file:  
> 
> 		android {
>	    	compileSdkVersion 23
>	    	buildToolsVersion "23.0.3"
>	
>	    defaultConfig {
>	        minSdkVersion 9
>	        targetSdkVersion 19
>	
>			//Enable RenderScript
>	        renderscriptTargetApi 23
>	        renderscriptSupportModeEnabled true
>	    	}
>		}
>

#[Download Demo APK](https://fir.im/yl3e)

#License

      MIT Copyright (c) 2017 codeestX

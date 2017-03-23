package com.chenld.mycloudreader;

import android.databinding.DataBindingUtil;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chenld.mycloudreader.app.ConstantsImageUrl;
import com.chenld.mycloudreader.databinding.ActivityMainBinding;
import com.chenld.mycloudreader.http.rx.RxBus;
import com.chenld.mycloudreader.http.rx.RxBusBaseMessage;
import com.chenld.mycloudreader.http.rx.RxCodeConstants;
import com.chenld.mycloudreader.ui.book.BookFragment;
import com.chenld.mycloudreader.ui.gank.GankFragment;
import com.chenld.mycloudreader.ui.menu.NavAboutActivity;
import com.chenld.mycloudreader.ui.menu.NavHomePageActivity;
import com.chenld.mycloudreader.ui.one.OneFragment;
import com.chenld.mycloudreader.utils.CommonUtils;
import com.chenld.mycloudreader.utils.ImgLoadUtil;
import com.chenld.mycloudreader.view.MyFragmentPagerAdapter;
import com.chenld.mycloudreader.view.statusbar.StatusBarUtil;

import java.util.ArrayList;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ActivityMainBinding mBinding;
    private FrameLayout llTitleMenu;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ViewPager vpContent;

    private ImageView llTitleGank;
    private ImageView llTitleOne;
    private ImageView llTitleDou;

    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initId();
        initRxBus();
        //无效果?
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, drawerLayout,
                CommonUtils.getColor(R.color.colorTheme));
        initContentFragment();
        initDrawerLayout();
        initListener();


    }

    private void initId() {
        drawerLayout = mBinding.drawerLayout;
        navView = mBinding.navView;
        fab = mBinding.include.fab;
        toolbar = mBinding.include.toolbar;
        llTitleMenu = mBinding.include.llTitleMenu;
        vpContent = mBinding.include.vpContent;
        fab.setVisibility(View.GONE);

        llTitleGank = mBinding.include.ivTitleGank;
        llTitleDou = mBinding.include.ivTitleDou;
        llTitleOne = mBinding.include.ivTitleOne;
    }

    /**
     * 每日推荐点击"新电影热映榜"跳转
     */
    //

    //RxBus.getDefault()._bus:既是订阅者（观察者），又是发布者(被观察者)
    //此处是做订阅者来处理发布者发布的事件
    //未做取消订阅的动作？
    private void initRxBus() {
        // 关联起来，这样就完成订阅.被观察者订阅观察者
        mSubscription = RxBus.getDefault()
                //过滤类型：过滤code为JUMP_TYPE_TO_ONE，类型为RxBusBaseMessage的被观察者
                .toObservable(RxCodeConstants.JUMP_TYPE_TO_ONE, RxBusBaseMessage.class)
                //订阅，与过滤后的RxBusBaseMessage进行订阅
                .subscribe(new Action1<RxBusBaseMessage>() {
                    @Override
                    public void call(RxBusBaseMessage integer) {
                        mBinding.include.vpContent.setCurrentItem(1);
                    }
                });
    }

    /**
     * 取消订阅
     */
    @Override
    protected void onDestroy() {
        super.onPause();
        if (!mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }
    //动态添加：将3大fragment加入到viewpage中
    private void initContentFragment() {
        ArrayList<Fragment> fragmentLists = new ArrayList<>();
        fragmentLists.add(new GankFragment());
        fragmentLists.add(new OneFragment());
        fragmentLists.add(new BookFragment());

        // 注意使用的是兼容包v4下：getSupportFragmentManager：得到的是activity对所包含fragment的Manager
        //3.0以下：getSupportFragmentManager()
        //3.0以上：getFragmentManager()
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentLists);
        vpContent.setAdapter(adapter);

        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);

        //设置默认选中的图片和选项
        mBinding.include.ivTitleGank.setSelected(true);
        vpContent.setCurrentItem(0);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            //启用HomeAsUp按钮，默认图标是一个返回箭头,类似于R.id.ll_title_menu的实现
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * inflateHeaderView 进来的布局要宽一些
     * 也可以通过在xml中通过如下实现
     * app:headerLayout="@layout/nav_header_main"
     * app:menu="@menu/activity_main_drawer"
     */
    private void initDrawerLayout() {
        navView.inflateHeaderView(R.layout.nav_header_main);
        //获得
        View headerView = navView.getHeaderView(0);

        ImageView ivAvatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        //访问失败
        ImgLoadUtil.displayCircle(ivAvatar, ConstantsImageUrl.IC_AVATAR);
        LinearLayout llNavHomepage = (LinearLayout) headerView.findViewById(R.id.ll_nav_homepage);
        LinearLayout llNavScanDownload = (LinearLayout) headerView.findViewById(R.id.ll_nav_scan_download);
        LinearLayout llNavDeedback = (LinearLayout) headerView.findViewById(R.id.ll_nav_deedback);
        LinearLayout llNavAbout = (LinearLayout) headerView.findViewById(R.id.ll_nav_about);
        LinearLayout llNavExit = (LinearLayout) headerView.findViewById(R.id.ll_nav_exit);
        llNavHomepage.setOnClickListener(this);
        llNavScanDownload.setOnClickListener(this);
        llNavDeedback.setOnClickListener(this);
        llNavAbout.setOnClickListener(this);
        llNavExit.setOnClickListener(this);
    }

    private void initListener() {
        llTitleMenu.setOnClickListener(this);
        mBinding.include.ivTitleGank.setOnClickListener(this);
        mBinding.include.ivTitleDou.setOnClickListener(this);
        mBinding.include.ivTitleOne.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                llTitleGank.setSelected(true);
                llTitleOne.setSelected(false);
                llTitleDou.setSelected(false);
                break;
            case 1:
                llTitleOne.setSelected(true);
                llTitleGank.setSelected(false);
                llTitleDou.setSelected(false);
                break;
            case 2:
                llTitleDou.setSelected(true);
                llTitleOne.setSelected(false);
                llTitleGank.setSelected(false);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_title_menu:// 开启菜单
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_title_gank:
                if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                    llTitleGank.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(0);
                }
                break;
            case R.id.iv_title_one:// 电影栏
                if (vpContent.getCurrentItem() != 1) {
                    llTitleOne.setSelected(true);
                    llTitleGank.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;
            case R.id.iv_title_dou:// 书籍栏
                if (vpContent.getCurrentItem() != 2) {
                    llTitleDou.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleGank.setSelected(false);
                    vpContent.setCurrentItem(2);
                }
                break;
            case R.id.ll_nav_homepage:// 主页
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                mBinding.drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NavHomePageActivity.startHome(MainActivity.this);
                    }
                }, 360);
                break;

            case R.id.ll_nav_scan_download://扫码下载
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                mBinding.drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        NavDownloadActivity.start(MainActivity.this);
                    }
                }, 360);
                break;
            case R.id.ll_nav_deedback:// 问题反馈
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                mBinding.drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        NavDeedBackActivity.start(MainActivity.this);
                    }
                }, 360);
                break;
            case R.id.ll_nav_about:// 关于云阅
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                mBinding.drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NavAboutActivity.start(MainActivity.this);
                    }
                }, 360);
                break;
            case R.id.ll_nav_exit:// 退出应用
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
//                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

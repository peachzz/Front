package com.terry.app.front;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.terry.app.front.bean.MenuItem;
import com.terry.app.front.fragment.GuanyuFragment;
import com.terry.app.front.fragment.NewsListFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MyAdapter adapter;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    final static MenuItem[] datas = new MenuItem[]{new MenuItem("文章", R.drawable.home), new MenuItem("关于", R.drawable.about), new MenuItem("退出", R.drawable.exit)};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.menu_item);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mToolbar.setTitle("技术前线");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        adapter = new MyAdapter();
        mListView.setAdapter(adapter);

        fragmentManager = getSupportFragmentManager();
        if (fragment == null) {
            fragment = new NewsListFragment();
            fragmentManager.beginTransaction().add(R.id.articles_container, fragment).commitAllowingStateLoss();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (datas[position].getIdRes()) {
                    case R.drawable.home:
                        fragmentManager.beginTransaction().replace(R.id.articles_container, fragment).commit();
                        break;
                    case R.drawable.about:
                        fragmentManager.beginTransaction().replace(R.id.articles_container, new GuanyuFragment()).commit();
                        break;
                    case R.drawable.exit:
                        exit();
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
            }
        });

    }

    private void exit() {
        new AlertDialog.Builder(this).setTitle("确认退出？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("取消", null).create().show();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.menu_item, null);
                viewHolder.mMenuIv = (ImageView) convertView.findViewById(R.id.menu_iv);
                viewHolder.mMenuTv = (TextView) convertView.findViewById(R.id.menu_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mMenuIv.setImageResource(datas[position].getIdRes());
            viewHolder.mMenuTv.setText(datas[position].getTitle());
            return convertView;
        }

        class ViewHolder {
            private ImageView mMenuIv;
            private TextView mMenuTv;
        }
    }
}

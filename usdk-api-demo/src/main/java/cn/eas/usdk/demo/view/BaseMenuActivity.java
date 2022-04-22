package cn.eas.usdk.demo.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.eas.usdk.demo.R;

public abstract class BaseMenuActivity extends BaseActivity {

    protected void initListView(final String[] titles, final int[] descriptionStringIds, final String[] classes) {
        List<Map<String, String>> listItems = new ArrayList<Map<String,String>>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, String> listItem = new HashMap<String, String>();
            listItem.put("title", titles[i]);
            listItem.put("description", getString(descriptionStringIds[i]));
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.main_listview,
                new String[] { "title", "description"},
                new int[] { R.id.titleLabel, R.id.descriptionLabel});


        final String packageName = getApplicationInfo().packageName;
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setAdapter(simpleAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                try {
                    startActivity(Class.forName(packageName + classes[position]));
                } catch (ClassNotFoundException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    showException(getString(R.string.coming_soon));
                }
            }
        });
    }
}
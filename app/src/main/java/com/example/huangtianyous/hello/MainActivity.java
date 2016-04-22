/*package com.example.huangtianyous.hello;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private List<Msg> msgList=new ArrayList<Msg>();
  //  private String[] data={"Jhon","XiaoBai"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
  //      ArrayAdapter<String> adapter = new ArrayAdapter<String>(
   //             MainActivity.this, android.R.layout.simple_list_item_1, data);
  //      ListView listView = (ListView) findViewById(R.id.friendList);
  //      listView.setAdapter(adapter);
  //  }
        initMsgs();
        adapter=new MsgAdapter(MainActivity.this,R.layout.msg_item,msgList);
        inputText=(EditText) findViewById(R.id.input_text);
        send=(Button) findViewById(R.id.send);
        msgListView=(ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content =inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg =new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                }
            }
        });
    }
    private void initMsgs(){
        Msg msg1=new Msg("Hello guy.", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2=new Msg("Hello. Who is that?", Msg.TYPE_SENT);
        msgList.add(msg2);
    }
}
*/
package com.example.katerina.mapsex;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.katerina.mapsex.datamodels.Game;
import com.example.katerina.mapsex.datamodels.Team;

import java.util.ArrayList;


public class TeamsActivity extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    public TeamsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        GameProvider provider= GameProvider.Initialize(new Game());
        Game game = provider.getGame();
        setTitle("Teams in game : " + game.name);

        final ListView listViewTeams = (ListView) findViewById(R.id.listTeams);
        ArrayList<Team> exampleList = Repository.getTeams(new Game());
        mAdapter = new TeamsAdapter(this, exampleList);
        listViewTeams.setAdapter(mAdapter);
        listViewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamsActivity.this, CertainTeamActivity.class);
                Team team = (Team) parent.getItemAtPosition(position);
                TeamProvider provider= TeamProvider.Initialize(team, true);
                startActivity(intent);
            }
        });
        findViewById(R.id.buttonCreateTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamsActivity.this, NewTeamActivity.class));
            }
        });

        findViewById(R.id.button_popup)
                //������ �� ��������� �� ������:
                .setOnClickListener(new View.OnClickListener() {

                    //������������ ������� ������ Button:
                    @Override
                    public void onClick(View view) {
                        //�������� popup ����, ��������� ��� � ����� popup.xml � �����������
                        //��������� ������� �� ������� OnMenuItemClickListener:
                        PopupMenu popup_menu = new PopupMenu(TeamsActivity.this, view);
                        popup_menu.setOnMenuItemClickListener(TeamsActivity.this);
                        popup_menu.inflate(R.menu.popup_menu);
                        popup_menu.show();
                    }
                });
    }

    //������������ ������� �� ������� popup ����, �������� �� id ������� ������, �������� � ����� popup.xml:
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_menu:
                //Toast.makeText(this, "������ ����� 1", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamsActivity.this, GamesActivity.class));
                return true;
            case R.id.map_menu:
                //Toast.makeText(this, "������ ����� 3", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamsActivity.this, DemoActivity.class));
                return true;
            case R.id.teams_menu:
                //Toast.makeText(this, "������ ����� 2", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamsActivity.this, TeamsActivity.class));
                return true;
            case R.id.rating_menu:
                startActivity(new Intent(TeamsActivity.this, RatingActivity.class));
                return true;
        }
        return true;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teams, menu);
        return true;
    }

/*    public void button2OnClick(View view){
        Intent intent1 = new Intent(TeamsActivity.this, MapsActivity.class);
        startActivity(intent1);
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

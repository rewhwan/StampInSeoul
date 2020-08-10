package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

// MoreActivity의 MoreAdapter 클래스

public class MoreAdapter extends BaseExpandableListAdapter {

    private ViewHolder viewHolder = null; // 뷰를 보관하는 viewHolder 변수를 선언, null 값을 준다.
    private LayoutInflater inflater = null; // 레이아웃.xml을 객체화 시키는 LayLayoutInflater 타입으로 inflater 변수를 선언한다.

    // ArrayList 함수를 String 타입으로 groupList 변수에 대입시킨다.
    private ArrayList<String> groupList;

    // ArrayList 함수에 또 다시 String 타입으로 늘여놓은 ArrayList 타입으로 chileList 변수에 대입시킨다.
    private ArrayList<ArrayList<String>> childList;

    // Constractor
    public MoreAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> chileList) {
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = chileList;
    }

    //-------------------------------------------------------------------------------------------//

    //Expendable ListView 부모 항목의 전체 갯수
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    //Expendable ListView 부모 항목의 데이터 정보
    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    //Expendable ListView 부모 항목의 데이터 위치 정보
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 최종적으로 부모 화면에 뿌려준다.
    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {

            viewHolder = new ViewHolder();

            // view에 뿌려줄 레이아웃 객체 more_item를 불러준다.
            convertView = inflater.inflate(R.layout.more_item, viewGroup, false);

            // more_item 안에 있는 UI 아이디 (txtContent, imgArrow)를 등록한다.
            viewHolder.txtContent = convertView.findViewById(R.id.txtContent);
            viewHolder.imgArrow = convertView.findViewById(R.id.imgArrow);

            // 이를 적용한다.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 리스트 뷰를 접고 펼쳤을때의 알맞는 이미지 등록하기.
        if (b) {
            viewHolder.imgArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        } else {
            viewHolder.imgArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }

        viewHolder.txtContent.setText(groupList.get(groupPosition)); // 제목 창에 groupList에 저장된 데이터를 보여주게 한다.

        return convertView; // 이를 돌려 모두 view에 보인다.

    }// end of getGroupView


    //-------------------------------------------------------------------------------------------//


    //Expendable ListView 자식 항목의 전체 갯수 (내 정보, 고객 센터, 이용 약관, App 정보, 개발자 정보)
    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    //Expendable ListView 자식 항목의 데이터 정보
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    //Expendable ListView 자식 항목의 데이터 위치 정보
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 최종적으로 자식 화면에 뿌려준다.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {

        // convertView가 null값 이라면...
        if (convertView == null){

            viewHolder = new ViewHolder(); // ViewHoler 객체를 새로 만든다.

            // view에 뿌려줄 레이아웃 객체 more_item_child를 불러준다.
            convertView = inflater.inflate(R.layout.more_item_child, viewGroup, false);

            // more_item_child의 UI ID를 등록한다.
            viewHolder.txtContentChild = convertView.findViewById(R.id.txtContentChild);
            convertView.setTag(viewHolder);

        // 혹은...
        } else {

            viewHolder = (ViewHolder) convertView.getTag(); // viewHoleder 객체에 이를 적용하기.
        }

        // txtContentChild에 childList의 데이터 정보를 띄운다.
        viewHolder.txtContentChild.setText(childList.get(groupPosition).get(childPosition));

        return convertView; // 이를 리턴하여 view에 띄운다.

    }// end of getChildView


    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

// 별도의 내부 클래스 ViewHolder 선언.
 public class ViewHolder {

     public ImageView imgArrow; // more_item 객체에 있는 UI
     public TextView txtContent; // more_item 객체에 있는 UI
     public TextView txtContentChild; // more_item_child 객체에 있는 UI
     public CircleImageView kakaoImageView;

    }// end of ViewHolder

}// end of class

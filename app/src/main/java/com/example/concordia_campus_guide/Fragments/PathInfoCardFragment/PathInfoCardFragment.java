package com.example.concordia_campus_guide.Fragments.PathInfoCardFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.concordia_campus_guide.Helper.PathFinder;
import com.example.concordia_campus_guide.Models.Direction;
import com.example.concordia_campus_guide.Models.PointType;
import com.example.concordia_campus_guide.Models.RoomModel;
import com.example.concordia_campus_guide.Models.TransitType;
import com.example.concordia_campus_guide.Models.WalkingPoint;
import com.example.concordia_campus_guide.R;
import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PathInfoCardFragment extends Fragment {
    private List<WalkingPoint> walkingPointList;
    private List<Direction> directionList;
    private double totalDistance = 0;

    private PathInfoCardViewModel mViewModel;

    public static PathInfoCardFragment newInstance() {
        return new PathInfoCardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.path_info_card_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PathInfoCardViewModel.class);
        walkingPointList = getWalkingPoints();
        populateDirections();
        calculateDistance();
    }

    private List<WalkingPoint> getWalkingPoints() {
        RoomModel src = new RoomModel(new Double[]{-73.57901685, 45.49761115}, "927", "H-9");
        RoomModel destination = new RoomModel(new Double[]{-73.57854277, 45.49739565}, "918", "H-8");
        PathFinder pf = new PathFinder(getContext(), src, destination);
        return pf.getPathToDestination();

    }

    public void populateDirections() {
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.paths_image_buttons);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        for (int i = 0; i < walkingPointList.size(); i++) {
            switch (walkingPointList.get(i).getPointType()) {
                case ELEVATOR:
                    if (i != 0 && walkingPointList.get(i - 1).getPointType() != PointType.ELEVATOR) {
                        createImageButton(layout, layoutParams, R.drawable.ic_elevator_white);
                        setDividerTextView(layout, layoutParams);
                    }
                    break;
                case STAIRS:
                    if (i != 0 && walkingPointList.get(i - 1).getPointType() != PointType.ELEVATOR) {
                        createImageButton(layout, layoutParams, R.drawable.ic_stairs_white);
                        setDividerTextView(layout, layoutParams);
                    }
                    break;
                case STAFF_ELEVATOR:
                    if (i != 0 && walkingPointList.get(i - 1).getPointType() != PointType.ELEVATOR) {
                        createImageButton(layout, layoutParams, R.drawable.ic_staff_elevator_white);
                        setDividerTextView(layout, layoutParams);
                    }
                    break;
            }
        }

        createImageButton(layout, layoutParams, R.drawable.ic_directions_walk_black_24dp);
    }

    public void createImageButton(LinearLayout layout, LinearLayout.LayoutParams layoutParams, int imageId) {
        ImageButton btn = new ImageButton(getContext());
        btn.setLayoutParams(layoutParams);
        btn.setImageResource(imageId);
        btn.setColorFilter(Color.rgb(147, 35, 57));
        btn.setBackgroundColor(getResources().getColor(R.color.toolbarIconColor));
        layout.addView(btn);
    }

    public void setDividerTextView(LinearLayout layout, LinearLayout.LayoutParams layoutParams) {
        TextView divider = new TextView(getContext());
        divider.setText(">");
        divider.setGravity(Gravity.CENTER);
        divider.setLayoutParams(layoutParams);
        layout.addView(divider);
    }

    public void calculateDistance() {
        directionList = new ArrayList<Direction>();
        Date startTime = Calendar.getInstance().getTime();
        Date endTime = new Date();

        for (int i=0; i<walkingPointList.size() - 1; i++) {
            WalkingPoint startWalkingPoint = walkingPointList.get(i);
            WalkingPoint endWalkingPoint = walkingPointList.get(i + 1);
            double distance = getDistanceFromLatLonInKm(startWalkingPoint.getCoordinate().getLatitude(), startWalkingPoint.getCoordinate().getLongitude(), endWalkingPoint.getCoordinate().getLatitude(), startWalkingPoint.getCoordinate().getLongitude());
            if (i !=0 ) {
                startTime = directionList.get(directionList.size() - 1).getEndTime();
            }
            // on average a person walks 5km / hr
            long timeTakenInSeconds = (long) (distance * 60 * 60 / 5);
            endTime.setTime(startTime.getTime() + TimeUnit.SECONDS.toMillis(timeTakenInSeconds));

            Direction direction = new Direction(startWalkingPoint.getCoordinate().getLatLng(), endWalkingPoint.getCoordinate().getLatLng(), TransitType.WALK, "", distance);
            direction.setStartTime(startTime);
            direction.setEndTime(endTime);
            directionList.add(direction);
            totalDistance += distance;
        }
    }

    public double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

}

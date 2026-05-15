package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

public class StateComponent implements Component, Pool.Poolable {
    public enum State { IDLE, WALK, PATROL, CHASE, RETURN }

    public State current = State.IDLE;
    public List<Vector2> waypoints = new ArrayList<>();
    public int waypointIndex = 0;
    public float lostSightTimer = 0f;
    public boolean isPlayer = false;
    public boolean isGuard = false;
    public boolean isValet = false;
    public float detectRadius = 150f;
    public float chaseSpeed = 90f;
    public float patrolSpeed = 60f;
    public float returnSpeed = 70f;
    public Vector2 spawnPoint = new Vector2();

    public List<Vector2> currentPath = new ArrayList<>();
    public int pathIndex = 0;
    public float repathTimer = 0f;
    public float stuckTimer = 0f;
    public float lastX = 0f;
    public float lastY = 0f;

    @Override
    public void reset() {
        current = State.IDLE;
        waypoints.clear();
        waypointIndex = 0;
        lostSightTimer = 0f;
        isPlayer = false;
        isGuard = false;
        isValet = false;
        detectRadius = 150f;
        chaseSpeed = 90f;
        patrolSpeed = 60f;
        returnSpeed = 70f;
        spawnPoint.set(0, 0);
        currentPath.clear();
        pathIndex = 0;
        repathTimer = 0f;
        stuckTimer = 0f;
        lastX = 0f;
        lastY = 0f;
    }
}

package com.notrotmg.shared;

public interface MovementStrategy {
    int calculateDx(int currentX, int inputDx, int speed);
    int calculateDy(int currentY, int inputDy, int speed);
}

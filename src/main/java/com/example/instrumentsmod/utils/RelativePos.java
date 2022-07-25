package com.example.instrumentsmod.utils;

public class RelativePos {
    public int front, up, right;
    public RelativePos(int front, int up, int right){
        this.front = front;
        this.up = up;
        this.right = right;
    }

    public RelativePos shifted(RelativePos shift){
        return new RelativePos(front + shift.front, up + shift.up, right + shift.right);
    }

    public RelativePos shifted(int front, int up, int right){
        return this.shifted(new RelativePos(front, up, right));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != RelativePos.class)
            return false;
        RelativePos pos = (RelativePos) obj;
        return front == pos.front && up == pos.up && right == pos.right;
    }

    @Override
    public int hashCode() {
        return front ^ up ^ right;
    }
}

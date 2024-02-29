package cn.monkey.data;

public record CmdTypeTuple(int index) {

    public long toMe() {
        return this.index + 1;
    }

    public long toOther() {
        return this.index + 2;
    }
}

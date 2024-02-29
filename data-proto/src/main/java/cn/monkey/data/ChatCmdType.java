package cn.monkey.data;

public interface ChatCmdType {

    CmdTypeTuple ENTER_GROUP = new CmdTypeTuple(10001);

    CmdTypeTuple EXIST_GROUP = new CmdTypeTuple(10004);

    CmdTypeTuple OFFLINE = new CmdTypeTuple(10007);
}

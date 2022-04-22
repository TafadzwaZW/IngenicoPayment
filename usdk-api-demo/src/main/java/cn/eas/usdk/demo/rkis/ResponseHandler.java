package cn.eas.usdk.demo.rkis;

import cn.eas.usdk.demo.rkis.message.Response;

/**
 * KMS响应数据处理器
 *
 * @author linll
 */
public interface ResponseHandler {

    /**
     * 请求处理响应
     * @param response 响应报文数据
     * @return true - 处理成功，继续接收，直至所有报文组接收完毕； false - 处理失败，无须接收报文，交易以失败结束
     */
    boolean handle(Response response);
}

# 命名空间，可以不写也可以写多个，按照使用服务端、客户端语言来写即可
namespace java com.hugmount.helloboot.thrift.server

# 定义一个用户结构
struct UserInfo {
    # 序号:字段类型 字段名
    1:i64 id
    2:string username
    3:string password
}

# 定义一个用户服务
service HelloService {
    # 定义一个getUser方法（接收一个用户id，返回上面定义的用户信息）
    UserInfo getUser(1:i32 id)

    # 方法定义格式：
    # 返回的类型 方法名(序号:参数类型 参数名 ... )
    # bool Test(1:i32 id, 2:string name, 3:i32 age ... )
}
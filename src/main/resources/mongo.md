1. 数据库与集合操作

# 查看所有数据库
show dbs

# 切换到指定数据库（不存在则创建）
use mydb

# 查看当前数据库中的集合
show collections

# 删除当前数据库
db.dropDatabase()

2. 插入文档（Insert）
# 插入单条文档
db.users.insertOne({
  name: "Alice",
  age: 25,
  email: "alice@example.com"
})

# 插入多条文档
db.users.insertMany([
  { name: "Bob", age: 30 },
  { name: "Charlie", age: 35 }
])


3. 查询文档（Find）
# 查询所有文档
db.users.find()

# 格式化输出（美观显示）
db.users.find().pretty()

# 查询第一条匹配的文档
db.users.findOne({ age: 25 })
条件查询
$eq	        db.users.find({ age: { $eq: 25 } })	等于
$gt, $gte	db.users.find({ age: { $gt: 25 } })	大于 / 大于等于
$lt, $lte	db.users.find({ age: { $lt: 30 } })	小于 / 小于等于
$in	        db.users.find({ age: { $in: [25, 30] } })	在数组中匹配
$ne	        db.users.find({ age: { $ne: 25 } })	不等于
$and, $or	db.users.find({ $or: [{ age: 25 }, { name: "Bob" }] })	逻辑或
$regex	    db.users.find({ name: { $regex: /^A/ } })	正则匹配（名字以A开头）

投影（返回指定字段）
# 只返回name和age字段，_id默认返回
db.users.find({}, { name: 1, age: 1, _id: 0 })

排序与分页
# 按age升序排序（1: 升序，-1: 降序）
db.users.find().sort({ age: 1 })

# 分页查询（skip跳过前10条，限制返回5条）
db.users.find().skip(10).limit(5)

# 统计文档数量
db.users.countDocuments({ age: { $gt: 25 } })

4. 更新文档（Update）
# 更新单条文档（只更新匹配的第一条）
db.users.updateOne(
  { name: "Alice" },
  { $set: { age: 26 } }  # 修改字段
)

# 更新多条文档
db.users.updateMany(
  { age: { $lt: 30 } },
  { $set: { status: "young" } }
)

# 增加字段
db.users.updateOne(
  { name: "Bob" },
  { $set: { hobbies: ["reading", "coding"] } }
)

# 自增字段
db.users.updateOne(
  { name: "Charlie" },
  { $inc: { age: 1 } }  # age += 1
)

5. 删除文档（Delete）
# 删除单条文档
db.users.deleteOne({ name: "Alice" })

# 删除多条文档
db.users.deleteMany({ age: { $gt: 30 } })

6. 聚合查询（Aggregate）
# 按age分组统计数量
db.users.aggregate([
  { $group: { _id: "$age", count: { $sum: 1 } } }
])

# 多阶段聚合：匹配 → 分组 → 排序
db.users.aggregate([
  { $match: { age: { $gt: 20 } } },  # 筛选
  { $group: { _id: "$name", total: { $sum: "$age" } } },  # 分组求和
  { $sort: { total: -1 } }  # 按total降序
])

7. 索引操作
# 创建单字段索引
db.users.createIndex({ name: 1 })  # 1: 升序，-1: 降序

# 查看集合索引
db.users.getIndexes()

# 删除索引
db.users.dropIndex("name_1")

8. 常见问题
查询性能慢？
为常用查询字段创建索引：db.collection.createIndex({ field: 1 })。

如何备份数据？
使用 mongodump 导出数据
mongodump --db mydb --out /backup

连接远程数据库？
mongo "mongodb://username:password@host:port/dbname"

9. 官方文档
https://www.mongodb.com/zh-cn/docs/manual/
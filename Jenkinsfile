// 脚本必须包含在一个pipeline块内. jenkins新建pipeline/流水线项目, 脚本定义选择from scm 填写git地址
pipeline {
    // 必须出现的指令,以在任意agent上执行pipeline
    agent any

    // 设置参数
    parameters {
        choice(name: 'ENV_TYPE', choices: ['test', 'uat', 'prod'], description: '构建环境: test, sit, uat, prod')
        string(name: 'GIT_BRANCH', defaultValue: 'master', description: '选择构建分支')
        string(name: 'GIT_COMMIT_VERSION', defaultValue: '', description: '选择发布的版本,git提交的版本号,只取前7位')
    }

    // 设置运行时的环境变量
    environment {
        // 不要加http
        HARBOR_ADDR = "www.fxitalk.com:9980";
        HARBOR_USERNAME = "zangbuge";
        HARBOR_PASSWORD = "Harbor.123";
        HARBOR_PROJECT_NAME = "helloboot";
        APP_NAME = "helloboot";

        // SSH Servers 多台服务器逗号分割, 值为jenkins中配置服务器name
        SIT_SERVERS_ADDR = "testhellogradle";
        UAT_SERVERS_ADDR = "addr";
        PROD_SERVERS_ADDR = "addr1,addr2,addr3";

    }

    // 定义Pipeline或stage运行结束时的操作
    // always：无论Pipeline运行的完成状态如何都会运行
    // changed：只有当前Pipeline 运行的状态与先前完成的Pipeline的状态不同时，才能运行
    // failure：仅当当前Pipeline处于“失败”状态时才运行
    // success：仅当当前Pipeline具有“成功”状态时才运行
    // unstable：只有当前Pipeline具有“不稳定”状态才能运行
    // aborted：只有当前Pipeline处于“中止”状态时才能运行
    post {
        success {
            echo 'doing pipeline success'
        }
        failure {
            echo 'doing pipeline fail'
        }
    }

    // 包含一个或多个stage的序列，Pipeline的大部分工作在此执行
    stages {
        stage('获取当前commit版本号') {
        	steps {
        		script {
        		    // 获取commit前7位版本号
        			env.GET_COMMIT_VERSION = sh (script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        		}
        	}
        }

        // 必须出现的指令,需要定义stage的名字
        stage ('打包上传镜像') {
            // 必须出现的指令,具体执行步骤
            steps {
                // 设置超时时间
                timeout (time: 30, unit: 'MINUTES') {
                    echo '开始打包应用'
                    sh '''
                        cd /var/jenkins_home/workspace/$APP_NAME
                        docker image build ./ -t $APP_NAME:${GET_COMMIT_VERSION}
                        docker login $HARBOR_ADDR -u $HARBOR_USERNAME -p $HARBOR_PASSWORD
                        docker tag $APP_NAME:${GET_COMMIT_VERSION} $HARBOR_ADDR/$HARBOR_PROJECT_NAME/$APP_NAME:${GET_COMMIT_VERSION}
                        docker push $HARBOR_ADDR/$HARBOR_PROJECT_NAME/$APP_NAME:${GET_COMMIT_VERSION}
                    '''
                }
            }
        }

        // 须安装 Publish over SSH 插件
        stage ('ssh拉取harbor镜像') {
            steps {
                script {
                    echo "当前发布环境: ${params.ENV_TYPE}"
                    // 遍历所有服务器
                    def publish_ssh_server_select = "$SIT_SERVERS_ADDR".split(",")
                    for (int j = 0; j < publish_ssh_server_select.length; j++) {
                        def currentServerName = publish_ssh_server_select[j]
                        echo "准备发布的ServerName:　$currentServerName"
                        sshPublisher(publishers: [sshPublisherDesc(configName: "${currentServerName}",
                                                transfers: [sshTransfer(cleanRemote: false, excludes: '',
                                                        execCommand: """
                                                            // 远程服务器 TODO
                                                            // 删除旧镜像
                                                            // 拉取新镜像
                                                            // 重启新容器
                                                            cd /app
                                                            touch test_jenkins.log
                                                            echo "该机器已部署完成ServerName: ${currentServerName}"
                                                        """,
                                                        execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false,
                                                        patternSeparator: '[, ]+', remoteDirectory: '/app/', remoteDirectorySDF: false, removePrefix: '',
                                                        sourceFiles: 'target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])

                    }
                    echo "所有机器已部署完成"

                }
            }
        }


    }


}
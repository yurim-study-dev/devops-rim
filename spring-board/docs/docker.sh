# 컨테이너가 구동중일 경우 삭제 후 다시 구동
docker stop mydb
docker stop board-web

docker rm mydb
docker rm board-web

# MySql DB 컨테이너 구동
docker run -d --name mydb --network my-net -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:9.7

# temurin 25 jre 컨테이너 구동
docker run -d --name board-web -p 80:8080 --network my-net eclipse-temurin:25-jre-alpine tail -f /dev/null

# 로컬의 빌드된 최종 코드를 컨테이너에 복사(로그인한 계정의 홈디렉토리로 복사)
docker cp ./build/libs/spring-board-0.0.1-SNAPSHOT.jar board-web:/root/board.jar

# 컨테이너 내부의 대화형 쉘 접속 후 java 명령어로 board.jar 실행
docker exec -it board-web //bin/sh
cd ~
java -jar board.jar



# spring-board 배포
# 1. 기존 컨테이너들 중지 및 삭제
docker stop spring-board-app db-server
docker rm spring-board-app db-server

# 2. 네트워크 삭제
docker network rm myapp-net

# 3. 네트워크 새로 생성
docker network create myapp-net

# 4. MySQL 컨테이너 먼저 실행
docker run --name db-server --network myapp-net -p 3306:3306 -e MYSQL_DATABASE=board_db -e MYSQL_ROOT_PASSWORD=root mysql:9.7

#다른계정으로 연결?
docker run --name db-server --network myapp-net -p 3306:3306 \
  -e MYSQL_DATABASE=board_db \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_USER=board-app \
  -e MYSQL_PASSWORD=Board123! \
  mysql:9.7
# (ready for connections 문구가 뜨면 Ctrl+C로 빠져나오기)

# 6. 스프링 부트 컨테이너 실행
docker run -d --name spring-board --network myapp-net -p 80:8080 \
  -e SPRING_DATASOURCE_USERNAME=board-app \
  -e SPRING_DATASOURCE_PASSWORD=Board123! \
  yurimweb/spring-board:1.0



# 테스트 완료된 spring-board 이미지를 운영 서버에 배포하기 위해서 docker hub에 업로드
# 로그인
docker login

# dccker hub 에 이미지 업로드
docker push yurimweb/spring-board:1.0


###############################################
#이미지생성
docker build -t yurimweb/spring-board:1.0 .

# 이미지 도커 허브에 업로드
docker push yurimweb/spring-board:1.0

# EC2에 MySQL DB 컨테이너 구동
docker run --name db-server --network myapp-net -p 3306:3306 -e MYSQL_DATABASE=board_db -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=board-app -e MYSQL_PASSWORD=Board123! mysql:9.7
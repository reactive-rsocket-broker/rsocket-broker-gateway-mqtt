# sub topic
sub:
  mosquitto_sub -v -i client007 -h 127.0.0.1 -p 1883 -t 'test/topic'

# pub message
pub:
  mosquitto_pub -h 127.0.0.1 -p 1883 -t 'test/topic' -m 'helloWorld'

rsocket_pub:
   rsc ws://localhost:9001/rsocket --request --route topic.test/topic -d HelloWorld --debug

# setup
setup:
  brew install mosquitto

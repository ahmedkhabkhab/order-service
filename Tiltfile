# Build
custom_build(ref='order-service', command='./mvnw compile jib:dockerBuild -Dimage=$EXPECTED_REF', deps=['pom.xml', 'src'])

# Deploy
k8s_yaml(['k8s/deployment.yml', 'k8s/service.yml'])

# Manage
k8s_resource('order-service', port_forwards=['9002'])

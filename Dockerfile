FROM airhacks/glassfish
COPY ./target/GardenDock.war ${DEPLOYMENT_DIR}

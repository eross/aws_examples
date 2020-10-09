# From SDK docs.  Create a new project.
mvn -B archetype:generate \
  -DarchetypeGroupId=software.amazon.awssdk \
  -DarchetypeArtifactId=archetype-lambda -Dservice=s3 -Dregion=US_WEST_2 \
  -DgroupId=com.hp.aasandbox.secrets \
  -DartifactId=secrets

### EasyMod, a Syson extension.

A Syson addon allowing to develop views and methodlogy from EasyMOD project. 

### Build
Execute the build.sh script available at the repository's root. Or alter it to you satisfaction / specification.

### Setup the Database

Docker is the preferred way to install and manage the PostgreSQL database used by `easyMod`. You can download Docker [here](https://www.docker.com/products/docker-desktop/). Note that Docker is already packaged on most Linux distributions. You can check that your shell is correctly configured by running `docker --version` in it.

Create and run the PostgreSQL container by running the following command:

----
docker run -p 5433:5432 --name irt-easymod-postgres \
                        -e POSTGRES_USER=dbuser \
                        -e POSTGRES_PASSWORD=dbpwd \
                        -e POSTGRES_DB=syson-db \
                        -d postgres:12

### Execution

First copy/paste the folder SysONExtensionDemo\distribution\src\main\resources.
Then add in the librariesEasyMod folder the executable jar easymod-distribution-2024.11.0-addon.jar located in SysONExtensionDemo\distribution\target\easymod-distribution-2024.11.0-addon.jar.
Download the SysON fatjar (syson-application-2024.11.0.jar) from https://github.com/eclipse-syson/syson/packages/2020337?version=2024.11.0 into the pasted folder.
Finally, execute the easymod.sh script available in your pasted folder.

### License
Eclipse Public License - v 2.0.
A copy is available at the repository's root.
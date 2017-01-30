FROM node:6.5.0

# Create app directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Bundle app source
COPY . /usr/src/app

# Install dependencies and set bower to use super user
RUN npm install --global bower gulp
RUN echo '{ "allow_root": true }' > /root/.bowerrc
RUN npm install && bower install
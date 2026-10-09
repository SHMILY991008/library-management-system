FROM node:16.20.2-alpine AS build

WORKDIR /app
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM nginx:1.14-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY deploy/nginx/docker.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

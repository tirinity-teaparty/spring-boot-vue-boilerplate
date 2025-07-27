// vue-common/deploy.js

const fs = require('fs');
const path = require('path');
const rimraf = require('rimraf');

process.env.NODE_ENV = 'production';

const buildDir = path.resolve(__dirname, './dist');

const destinationProjects = ['spring-boot-java-vue', 
  // 'spring-boot-kotlin-vue'
];

function copyDirectorySync(source, destination) {
  if (!fs.existsSync(destination)) {
    fs.mkdirSync(destination, { recursive: true });
  }

  const files = fs.readdirSync(source);

  for (const file of files) {
    const sourcePath = path.join(source, file);
    const destinationPath = path.join(destination, file);

    if (fs.lstatSync(sourcePath).isDirectory()) {
      copyDirectorySync(sourcePath, destinationPath);
    } else {
      fs.copyFileSync(sourcePath, destinationPath);
    }
  }
}

for (const project of destinationProjects) {
  const dest = path.resolve(__dirname, `../${project}/src/main/resources/static`);
  rimraf.sync(dest);
  fs.mkdirSync(dest, { recursive: true });
  copyDirectorySync(buildDir, dest);

  console.log(`Copied Vue build outputs to ${project}`);
}

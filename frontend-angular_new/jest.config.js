module.exports = {
  preset: 'jest-preset-angular',
  globalSetup: 'jest-preset-angular/global-setup',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  testEnvironment: 'jsdom',
  testMatch: ['<rootDir>/src/**/*.spec.ts'],
  transform: {
    '^.+\\.(ts|mjs|js|html)$': [
      'jest-preset-angular',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
        stringifyContentPathRegex: '\\.(html|svg)$',
        isolatedModules: true,
      },
    ],
  },
  transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)'],
  moduleNameMapper: {
    '^src/(.*)$': '<rootDir>/src/$1',
    '\\.(css|scss|sass|less)$': 'identity-obj-proxy',
  },
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
};

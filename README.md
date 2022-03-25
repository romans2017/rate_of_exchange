# Telegram chatbot Exchange Rates

This Telegram chatbot is designed to receive data about rates USD, EUR, PLN to UAH from some banks.

To start chat bot you should define enviroment variables:
- botName - bot name as defined in Telegram @BotFather
- botToken - bot token as defined in Telegram @BotFather

Chatbot has settings:
- decimal point number
- bank
- currency
- notification time

All these settings save in AWS or Google Drive.

To save in AWS ([S3 Service](https://aws.amazon.com/s3**)) you should define enviroment variables:
- AWS_ACCESS_KEY_ID - key ID of [IAM](https://aws.amazon.com/iam/?nc2=type_a**) user
- AWS_SECRET_ACCESS_KEY - access key of [IAM](https://aws.amazon.com/iam/?nc2=type_a**) user
- AWS_REGION - region of ([S3 bucket](https://aws.amazon.com/s3**))

To save in [Google Drive](https://developers.google.com/drive/api/guides/enable-drive-api**):
- GOOGLE_CREDENTIALS - JSON string or path to file with JSON string which contains credentials to [Google Drive](https://developers.google.com/workspace/guides/create-credentials#service-account**)

Active bot - @currensyChatBot

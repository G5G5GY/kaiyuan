import { theme } from 'antd';
import { lightDefaultTheme } from '.';
export const defaultAlgorithm = {
  token: {
    borderRadius: 2,
    colorPrimary: lightDefaultTheme.primary,
    fontSize: 14,
    // colorBgBase: lightDefaultTheme.backgroundColor.bg1,
  },
  components: {
    Layout: {
      bodyBg: lightDefaultTheme.backgroundColor.bg1,
      headerBg: lightDefaultTheme.backgroundColor.bgHeader,
    },
    Button: {
      fontSize: 14,
    },
    Table: {
      borderRadius: 0,
      borderRadiusLG: 0,
      padding: 10,
      paddingXS: 5,
      margin: 0,
      fontSize: 14,
      colorBorderSecondary: lightDefaultTheme.borderColor.bl1,
      paddingContentVerticalLG: 4,
    },
    Modal: {
      borderRadiusLG: 2,
      borderRadiusSM: 2,
      colorText: lightDefaultTheme.fontColor.fc3,
      borderRadius: 2,
      paddingContentHorizontalLG: 0,
      paddingMD: 0,
    },
    Menu: {
      itemBg: lightDefaultTheme.backgroundColor.bg1,
      activeBarWidth: 0,
      activeBarHeight: 0,
      activeBarBorderWidth: 0,
      subMenuItemBorderRadius: 8,
      horizontalItemBorderRadius: 8,
      itemBorderRadius: 8,
    },
  },
  algorithm: theme.defaultAlgorithm,
};
